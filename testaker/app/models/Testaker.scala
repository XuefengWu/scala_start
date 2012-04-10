package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


 
    case class Testaker(id:Pk[Long] = NotAssigned, name:String)  {
      override def toString = name
    }
    object Testaker {
  val simple = {
      get[Pk[Long]]("testaker.id") ~
      get[String]("testaker.name") map {
        case id~name => Testaker(id, name)
      }
    }
    /**
* Return a page of Testaker.
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[Testaker] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val testakers = SQL(
    """
      select * from testaker
      where (testaker.name like {filter})
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(Testaker.simple *)

  val totalRows = SQL(
    """
      select count(*) from testaker
      where  (testaker.name like {filter})
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(testakers, page, offest, totalRows)

}

}

    def create(v:Testaker) {
      DB.withConnection { implicit connection =>
        SQL("insert into testaker (id,name) values ((select next value for testaker_id_seq),{name})").on(
        'name -> v.name
        ).executeUpdate()
      }
    }
    

  def findById(id: Long): Option[Testaker] = { DB.withConnection { implicit c =>
      SQL("select * from testaker where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    

      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from testaker where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      

    def update(id: Long, v: Testaker) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update testaker set name = {name} where id = {id}"
          ).on(
            'id -> id,
            'name -> v.name
          ).executeUpdate()
        }
      }
    

      /**
       * Construct the Map[String,String] needed to fill a select options set.
       */
      def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from testaker").as(Testaker.simple *).map(c => c.id.toString -> c.toString)
      }
   }