package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


    
/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}
    case class Node(id:Pk[Long] = NotAssigned, title:String){
      override def toString = title
    }
    object Node {
  val simple = {
      get[Pk[Long]]("node.id") ~
      get[String]("node.title") map {
        case id~title => Node(id, title)
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
      where (node.title like {filter})
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
      where  (node.title like {filter})
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(nodes, page, offest, totalRows)

}

}

    def create(v:Node) {
      DB.withConnection { implicit connection =>
        SQL("insert into node (id,title) values ((select next value for node_id_seq),{title})").on(
        'title -> v.title
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
            "update node set title = {title} where id = {id}"
          ).on(
            'id -> id,
            'title -> v.title
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