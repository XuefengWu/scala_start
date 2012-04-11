package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import java.sql.Timestamp
import util.TimeStampColumn.rowToTimestamp


    case class Node(id:Pk[Long] = NotAssigned, createdAt:Timestamp = new Timestamp(System.currentTimeMillis()),lastUpdateAt:Timestamp = new Timestamp(System.currentTimeMillis()))
    object Node {
  val simple = {
      get[Pk[Long]]("node.id") ~
      get[Timestamp]("node.createdAt") ~
	get[Timestamp]("node.lastUpdateAt") map {
        case id~createdAt~lastUpdateAt => Node(id, createdAt,lastUpdateAt)
      }
    }
    /**
* Return a page of Node.
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[Node] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val nodes = SQL(
    """
      select * from node
      where  1 = 1 
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(Node.simple *)

  val totalRows = SQL(
    """
      select count(*) from node
      where   1 = 1 
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(nodes, page, offest, totalRows)

}

}

    def create(v:Node) {
      DB.withConnection { implicit connection =>
        SQL("insert into node (id,createdAt,lastUpdateAt) values ((select next value for node_id_seq),{createdAt},{lastUpdateAt})").on(
        'createdAt -> v.createdAt,
	'lastUpdateAt -> v.lastUpdateAt
        ).executeUpdate()
      }
    }
    

  def findById(id: Long): Option[Node] = { DB.withConnection { implicit c =>
      SQL("select * from node where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    

      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from node where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      

    def update(id: Long, v: Node) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update node set createdAt = {createdAt},lastUpdateAt = {lastUpdateAt} where id = {id}"
          ).on(
            'id -> id,
            'createdAt -> v.createdAt,
	'lastUpdateAt -> v.lastUpdateAt
          ).executeUpdate()
        }
      }
    

      /**
       * Construct the Map[String,String] needed to fill a select options set.
       */
      def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from node").as(Node.simple *).map(c => c.id.toString -> c.toString)
      }
   }