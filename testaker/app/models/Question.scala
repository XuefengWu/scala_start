package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs


case class Question(id: Pk[Long] = NotAssigned, nodeId: Long, themeId: Long,examId:Option[Long]=None,examQuestionId:Option[Long]=None, description: Option[String], note: Option[String],choices: Seq[Choice] = Nil){
  import libs.json.Json

  def toJson() = Json.toJson(Map(
    "id" -> Json.toJson(id.get),
    "desc" -> Json.toJson(description.getOrElse("--")),
    "note" -> Json.toJson(note.getOrElse("")),
    "examId" -> Json.toJson(examId.getOrElse(0.toLong)), 
    "examQuestionId" -> Json.toJson(examQuestionId.getOrElse(0.toLong)),
    "choices" -> Json.toJson(
      choices.map{ c =>
        Json.toJson(Map(
          "title" -> Json.toJson(c.title),
          "id" -> Json.toJson(c.id.get),
          "correct" -> Json.toJson(c.correct.getOrElse(false)),
          "note" -> Json.toJson(c.note.getOrElse("")),
          "examId" -> Json.toJson(examId.getOrElse(0.toLong)),
          "examQuestionId" -> Json.toJson(examQuestionId.getOrElse(0.toLong)),
          "questionId" -> Json.toJson(c.questionId)
        ))
      }
    )
  ))

}

object Question {

  case class QuestionChoice(id: Pk[Long] = NotAssigned, nodeId: Long, themeId: Long,examId:Option[Long]=None,examQuestionId:Option[Long]=None, description: Option[String], note: Option[String], choice: Choice)

  val withChoice = {
    get[Pk[Long]]("question.id") ~
      get[Long]("question.node_id") ~
      get[Long]("question.theme_id") ~
      get[Option[Long]]("exam.id") ~
      get[Option[Long]]("examQuestion.id") ~
      get[Option[String]]("question.description") ~
      get[Option[String]]("question.note") ~
      Choice.simple map {
      case id ~ nodeId ~ themeId ~ examId~examQuestionId~description ~note ~ choice => QuestionChoice(id, nodeId, themeId, examId,examQuestionId,description,note,choice)
    }
  }


  def list: Seq[Question] = {
    val choices: Seq[QuestionChoice] =
      DB.withConnection {
        implicit c =>
          SQL(
            """
              select *
              from question,theme,choice,exam,examQuestion
              where question.theme_id = theme.id
              and examQuestion.question_id = question.id
              and examQuestion.exam_id = exam.id
              and choice.question_id = question.id
              and exam.id = 1
            """
          ).as(withChoice *)
      }

    val questions = choices.groupBy(c => (c.id, c.nodeId,c.themeId,c.examId,c.examQuestionId, c.description,c.note)) map {
      case (k, v) => Question(k._1, k._2, k._3, k._4,k._5,k._6,k._7,v.map(_.choice))
    }
    questions.toSeq
  }


  def findById(eqId: Long): Option[Question] = {
    DB.withConnection {
      implicit c =>
        val choices: Seq[QuestionChoice] =
          DB.withConnection {
            implicit c =>
              SQL(
                """
                  select *
                  from question,theme,choice,examQuestion,exam
                  where question.theme_id = theme.id
                  and choice.question_id = question.id
                  and examQuestion.question_id = question.id
                  and examQuestion.exam_id = exam.id
                  and examQuestion.id = {eqId}
                """
              ).on('eqId -> eqId).as(withChoice *)
          }
        choices.headOption.map {
          c: QuestionChoice =>
            Question(c.id, c.nodeId,c.themeId, c.examId,c.examQuestionId, c.description,c.note,choices.map(_.choice))
        }
    }
  }


  def create(v: Question) {
    DB.withConnection {
      implicit connection =>
        SQL("insert into question (node_id,theme_id,description,note) values ({node_id},{theme_id},{description},{note})").on(
          'node_id -> v.nodeId,
          'theme_id -> v.themeId,
          'description -> v.description,
          'note -> v.note
        ).executeUpdate()
    }
  }




  def delete(id: Long) {
    DB.withConnection {
      implicit c =>
        SQL("delete from question where id = {id}").on(
          'id -> id
        ).executeUpdate()
    }
  }


  def update(id: Long, v: Question) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          "update question set node_id = {node_id},theme_id = {theme_id},description = {description},note = {note} where id = {id}"
        ).on(
          'id -> id,
          'node_id -> v.nodeId,
          'theme_id -> v.themeId,
          'description -> v.description,
          'note -> v.note
        ).executeUpdate()
    }
  }

}
