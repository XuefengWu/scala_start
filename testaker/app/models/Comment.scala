package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs


case class Comment(id: Pk[Long] = NotAssigned, nodeId: Long, replyToId: Long, context: String){
  import libs.json.Json
  def toJson() = Json.toJson(Map(
    "id" -> Json.toJson(id.get),
    "nodeId" -> Json.toJson(nodeId),
    "context" -> Json.toJson(context)
  ))

}

object Comment {
  val simple = {
    get[Pk[Long]]("comment.id") ~
      get[Long]("comment.node_id") ~
      get[Long]("comment.replyto_id") ~
      get[String]("comment.context") map {
      case id ~ nodeId ~ replytoId ~ context => Comment(id, nodeId, replytoId, context)
    }
  }

  def create(replyToId:Long, context:String) ={
    DB.withConnection {
      implicit connection =>
        val nodeId = Node().create()
        SQL("insert into comment (node_id,replyto_id,context) values ({node_id},{replyto_id},{context})").on(
          'node_id -> nodeId,
          'replyto_id -> replyToId,
          'context -> context
        ).executeUpdate()
        val id = SQL("SELECT LAST_INSERT_ID()").as(scalar[Long].single)
        findById(id)

    }
  }


  def findById(id: Long): Option[Comment] = {
    DB.withConnection {
      implicit c =>
        SQL("select * from comment where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }

  def forQuestion(id: Long):Seq[Comment] = {
    DB.withConnection {
      implicit c =>
        SQL("""
        select comment.*
        from comment, node as cnode,question,node as qnode
        where question.node_id = qnode.id
        and comment.node_id = cnode.id
        and comment.replyTo_id = qnode.id
        and question.id = {id}
        order by comment.id desc
        """).on('id -> id).as(Comment.simple *)
    }
  }

  def delete(id: Long) {
    DB.withConnection {
      implicit c =>
        SQL("delete from comment where id = {id}").on(
          'id -> id
        ).executeUpdate()
    }
  }


  def update(id: Long, v: Comment) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          "update comment set node_id = {node_id},replyto_id = {replyto_id},context = {context} where id = {id}"
        ).on(
          'id -> id,
          'node_id -> v.nodeId,
          'replyto_id -> v.replyToId,
          'context -> v.context
        ).executeUpdate()
    }
  }


  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = DB.withConnection {
    implicit connection =>
      SQL("select * from comment").as(Comment.simple *).map(c => c.id.toString -> c.toString)
  }

}