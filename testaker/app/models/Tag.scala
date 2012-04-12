package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


    case class Tag(id:Pk[Long] = NotAssigned, name:String,note:Option[String],themeId:Long)
    object Tag {
  val simple = {
      get[Pk[Long]]("tag.id") ~
      get[String]("tag.name") ~
      get[Option[String]]("tag.note") ~
	get[Long]("tag.theme_id") map {
        case id~name~note~themeId => Tag(id, name,note,themeId)
      }
    }
    
    /**
     * Parse a (Tag,Theme) from a ResultSet
     */
    val withReference = Tag.simple ~ Theme.simple map {
      case  tag~theme => (tag,theme)
    }
    /**
* Return a page of (Tag,Theme).
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Tag,Theme)] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val tags = SQL(
    """
      select * from tag
      left join Theme on tag.theme_id = theme.id
      where (tag.name like {filter})
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(Tag.withReference *)

  val totalRows = SQL(
    """
      select count(*) from tag
      left join Theme on tag.theme_id = theme.id
      where  (tag.name like {filter})
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(tags, page, offest, totalRows)

}

}

    def create(v:Tag) {
      DB.withConnection { implicit connection =>
        SQL("insert into tag (name,note,theme_id) values ({name},{note},{theme_id})").on(
        'name -> v.name,
        'note -> v.note,
	'theme_id -> v.themeId
        ).executeUpdate()
      }
    }
    

  def findById(id: Long): Option[Tag] = { DB.withConnection { implicit c =>
      SQL("select * from tag where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    

      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from tag where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      

    def update(id: Long, v: Tag) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update tag set name = {name},note = {note}, theme_id = {theme_id} where id = {id}"
          ).on(
            'id -> id,
            'name -> v.name,
            'note -> v.note,
	'theme_id -> v.themeId
          ).executeUpdate()
        }
      }
    

      /**
       * Construct the Map[String,String] needed to fill a select options set.
       */
      def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from tag").as(Tag.simple *).map(c => c.id.toString -> c.toString)
      }
   }