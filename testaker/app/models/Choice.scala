package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current



    case class Choice(id:Pk[Long] = NotAssigned, nodeId:Long,questionId:Long,title:String,note:Option[String],correct:Option[Boolean])
    object Choice {
  val simple = {
      get[Pk[Long]]("choice.id") ~
      get[Long]("choice.node_id") ~
	get[Long]("choice.question_id") ~
	get[String]("choice.title") ~
        get[Option[String]]("choice.note") ~
	get[Option[Boolean]]("choice.correct") map {
        case id~nodeId~questionId~title~note~correct => Choice(id, nodeId,questionId,title,note,correct)
      }
    }
    
    /**
     * Parse a (Choice,Node,Question) from a ResultSet
     */
    val withReference = Choice.simple ~ Node.simple  map {
      case  choice~node => (choice,node )
    }
    /**
* Return a page of (Choice,Node,Question).
*
* @param page Page to display
* @param pageSize Number of books per page
* @param orderBy used for sorting
* @param filter Filter applied on the name column
*/
def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Choice,Node )] = {

val offest = pageSize * page

DB.withConnection { implicit connection =>

  val choices = SQL(
    """
      select * from choice
      left join Node on choice.node_id = node.id
      left join Question on choice.question_id = question.id
      where (choice.title like {filter})
      order by {orderBy} nulls last
      limit {pageSize} offset {offset}
    """
  ).on(
    'pageSize -> pageSize,
    'offset -> offest,
    'filter -> filter,
    'orderBy -> orderBy
  ).as(Choice.withReference *)

  val totalRows = SQL(
    """
      select count(*) from choice
      left join Node on choice.node_id = node.id
      left join Question on choice.question_id = question.id
      where  (choice.title like {filter})
    """
  ).on(
    'filter -> filter
  ).as(scalar[Long].single)

  Page(choices, page, offest, totalRows)

}

}

    def create(v:Choice) {
      DB.withConnection { implicit connection =>
        SQL("insert into choice (node_id,question_id,title,correct) values ({node_id},{question_id},{title},{note},{correct})").on(
        'node_id -> v.nodeId,
	'question_id -> v.questionId,
	'title -> v.title,
        'note -> v.note,
	'correct -> v.correct
        ).executeUpdate()
      }
    }
    

  def findById(id: Long): Option[Choice] = { DB.withConnection { implicit c =>
      SQL("select * from choice where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    

      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from choice where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      

    def update(id: Long, v: Choice) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update choice set node_id = {node_id},question_id = {question_id},title = {title},note = {note}, correct = {correct} where id = {id}"
          ).on(
            'id -> id,
            'node_id -> v.nodeId,
	'question_id -> v.questionId,
	'title -> v.title,
          'note -> v.note,
	'correct -> v.correct
          ).executeUpdate()
        }
      }
    

      /**
       * Construct the Map[String,String] needed to fill a select options set.
       */
      def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from choice").as(Choice.simple *).map(c => c.id.toString -> c.toString)
      }
   }