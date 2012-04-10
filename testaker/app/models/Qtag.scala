package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

 
    case class Qtag(id:Pk[Long] = NotAssigned, questionId:Long,tagId:Long)
    object Qtag {
  val simple = {
      get[Pk[Long]]("qtag.id") ~
      get[Long]("qtag.question_id") ~
	get[Long]("qtag.tag_id") map {
        case id~questionId~tagId => Qtag(id, questionId,tagId)
      }
    }
    
    /**
     * Parse a (Qtag,Question,Tag) from a ResultSet
     */
    val withReference = Qtag.simple ~ Question.simple ~ Tag.simple map {
      case  qtag~question~tag => (qtag,question,tag)
    }
    /**
* Return a page of (Qtag,Question,Tag).
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Qtag,Question,Tag)] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val qtags = SQL(
    """
      select * from qtag
      left join Question on qtag.question_id = question.id
      left join Tag on qtag.tag_id = tag.id
      where  1 = 1 
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(Qtag.withReference *)

  val totalRows = SQL(
    """
      select count(*) from qtag
      left join Question on qtag.question_id = question.id
      left join Tag on qtag.tag_id = tag.id
      where   1 = 1 
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(qtags, page, offest, totalRows)

}

}

    def create(v:Qtag) {
      DB.withConnection { implicit connection =>
        SQL("insert into qtag (id,question_id,tag_id) values ((select next value for qtag_id_seq),{question_id},{tag_id})").on(
        'question_id -> v.questionId,
	'tag_id -> v.tagId
        ).executeUpdate()
      }
    }
    

  def findById(id: Long): Option[Qtag] = { DB.withConnection { implicit c =>
      SQL("select * from qtag where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    

      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from qtag where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      

    def update(id: Long, v: Qtag) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update qtag set question_id = {question_id},tag_id = {tag_id} where id = {id}"
          ).on(
            'id -> id,
            'question_id -> v.questionId,
	'tag_id -> v.tagId
          ).executeUpdate()
        }
      }
    

      /**
       * Construct the Map[String,String] needed to fill a select options set.
       */
      def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from qtag").as(Qtag.simple *).map(c => c.id.toString -> c.toString)
      }
   }