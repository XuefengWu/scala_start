package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


    case class Exam(id:Pk[Long] = NotAssigned, name:String,testakerId:Long,themeId:Long)
    object Exam {
  val simple = {
      get[Pk[Long]]("exam.id") ~
      get[String]("exam.name") ~
	get[Long]("exam.testaker_id") ~
        get[Long]("exam.theme_id")map {
        case id~name~testakerId~themeId => Exam(id, name,testakerId,themeId)
      }
    }
    
    /**
     * Parse a (Exam,Testaker) from a ResultSet
     */
    val withReference = Exam.simple ~ Testaker.simple ~ Theme.simple map {
      case  exam~testaker~theme => (exam,testaker,theme)
    }
    /**
* Return a page of (Exam,Testaker,Theme).
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Exam,Testaker,Theme)] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val exams = SQL(
    """
      select * from exam
      left join Testaker on exam.testaker_id = testaker.id
      left join Theme on exam.theme_id = theme.id
      where (exam.name like {filter})
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(Exam.withReference *)

  val totalRows = SQL(
    """
      select count(*) from exam
      left join Testaker on exam.testaker_id = testaker.id
      left join Theme on exam.theme_id = theme.id
      where  (exam.name like {filter})
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(exams, page, offest, totalRows)

}

}

    def create(v:Exam) {
      DB.withConnection { implicit connection =>
        SQL("insert into exam (name,testaker_id,theme_id) values ({name},{testaker_id},{theme_id})").on(
        'name -> v.name,
	      'testaker_id -> v.testakerId,
        'theme_id -> v.themeId
        ).executeUpdate()
      }
    }
    

  def findById(id: Long): Option[Exam] = { DB.withConnection { implicit c =>
      SQL("select * from exam where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    

      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from exam where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      

    def update(id: Long, v: Exam) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update exam set name = {name},testaker_id = {testaker_id},theme_id = {theme_id} where id = {id}"
          ).on(
            'id -> id,
            'name -> v.name,
	'testaker_id -> v.testakerId,
          'theme_id -> v.themeId
          ).executeUpdate()
        }
      }
    

      /**
       * Construct the Map[String,String] needed to fill a select options set.
       */
      def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from exam").as(Exam.simple *).map(c => c.id.toString -> c.toString)
      }
   }