package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current



    case class ExamQuestion(id:Pk[Long] = NotAssigned, examId:Long,questionId:Long)
    object ExamQuestion {
  val simple = {
      get[Pk[Long]]("examquestion.id") ~
      get[Long]("examquestion.exam_id") ~
	get[Long]("examquestion.question_id") map {
        case id~examId~questionId => ExamQuestion(id, examId,questionId)
      }
    }
    


    def create(v:ExamQuestion) {
      DB.withConnection { implicit connection =>
        SQL("insert into examquestion (id,exam_id,question_id) values ((select next value for examquestion_id_seq),{exam_id},{question_id})").on(
        'exam_id -> v.examId,
	'question_id -> v.questionId
        ).executeUpdate()
      }
    }
    

  def findById(id: Long): Option[ExamQuestion] = { DB.withConnection { implicit c =>
      SQL("select * from examquestion where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    

      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from examquestion where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      

    def update(id: Long, v: ExamQuestion) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update examquestion set exam_id = {exam_id},question_id = {question_id} where id = {id}"
          ).on(
            'id -> id,
            'exam_id -> v.examId,
	'question_id -> v.questionId
          ).executeUpdate()
        }
      }
    

      /**
       * Construct the Map[String,String] needed to fill a select options set.
       */
      def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from examquestion").as(ExamQuestion.simple *).map(c => c.id.toString -> c.toString)
      }
   }