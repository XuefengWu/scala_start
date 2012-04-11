package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


    case class Theme(id:Pk[Long] = NotAssigned, name:String)
    object Theme {
  val simple = {
      get[Pk[Long]]("theme.id") ~
      get[String]("theme.name") map {
        case id~name => Theme(id, name)
      }
    }
    /**
* Return a page of Theme.
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[Theme] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val themes = SQL(
    """
      select * from theme
      where (theme.name like {filter})
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(Theme.simple *)

  val totalRows = SQL(
    """
      select count(*) from theme
      where  (theme.name like {filter})
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(themes, page, offest, totalRows)

}

}

    def create(v:Theme) {
      DB.withConnection { implicit connection =>
        SQL("insert into theme (id,name) values ((select next value for theme_id_seq),{name})").on(
        'name -> v.name
        ).executeUpdate()
      }
    }
    

  def findById(id: Long): Option[Theme] = { DB.withConnection { implicit c =>
      SQL("select * from theme where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    

      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from theme where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      

    def update(id: Long, v: Theme) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update theme set name = {name} where id = {id}"
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
        SQL("select * from theme").as(Theme.simple *).map(c => c.id.toString -> c.toString)
      }
   }