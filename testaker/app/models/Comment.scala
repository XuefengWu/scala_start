package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


    
 
    case class Comment(id:Pk[Long] = NotAssigned, nodeId:Long,replytoId:Option[Long])
    object Comment {
  val simple = {
      get[Pk[Long]]("comment.id") ~
      get[Long]("comment.node_id") ~
	get[Option[Long]]("comment.replyto_id") map {
        case id~nodeId~replytoId => Comment(id, nodeId,replytoId)
      }
    }
    
    /**
     * Parse a (Comment,Node,Option[Node]) from a ResultSet
     */
    val withReference = Comment.simple ~ Node.simple ~ (Node.simple ?) map {
      case  comment~node~replyTo => (comment,node,replyTo)
    }
    /**
* Return a page of (Comment,Node,Option[Node]).
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Comment,Node,Option[Node])] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val comments = SQL(
    """
      select * from comment
      left join node n1 on comment.node_id = n1.id
      left join node n2 on comment.replyto_id = n2.id
      where  1 = 1 
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(Comment.withReference *)

  val totalRows = SQL(
    """
      select count(*) from comment
      left join node n1 on comment.node_id = n1.id
      left join node n2 on comment.replyto_id = n2.id
      where   1 = 1 
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(comments, page, offest, totalRows)

}

}

    def create(v:Comment) {
      DB.withConnection { implicit connection =>
        SQL("insert into comment (id,node_id,replyto_id) values ((select next value for comment_id_seq),{node_id},{replyto_id})").on(
        'node_id -> v.nodeId,
	'replyto_id -> v.replytoId
        ).executeUpdate()
      }
    }
    

  def findById(id: Long): Option[Comment] = { DB.withConnection { implicit c =>
      SQL("select * from comment where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    

      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from comment where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      

    def update(id: Long, v: Comment) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update comment set node_id = {node_id},replyto_id = {replyto_id} where id = {id}"
          ).on(
            'id -> id,
            'node_id -> v.nodeId,
	'replyto_id -> v.replytoId
          ).executeUpdate()
        }
      }
    

      /**
       * Construct the Map[String,String] needed to fill a select options set.
       */
      def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from comment").as(Comment.simple *).map(c => c.id.toString -> c.toString)
      }
   }