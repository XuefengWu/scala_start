package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs


case class Question(id: Pk[Long] = NotAssigned, nodeId: Long, themeId: Long, description: Option[String], note: Option[String],choices: Seq[Choice] = Nil){
  import libs.json.Json

  def toJson() = Json.toJson(Map(
    "id" -> Json.toJson(id.get),
    "desc" -> Json.toJson(description.getOrElse("--")),
    "choices" -> Json.toJson(
      choices.map{ c =>
        Json.toJson(Map(
          "title" -> Json.toJson(c.title),
          "id" -> Json.toJson(c.id.get),
          "correct" -> Json.toJson(c.correct.getOrElse(false)),
          "questionId" -> Json.toJson(c.questionId)
        ))
      }
    )
  ))

}

object Question {

  case class QuestionChoice(id: Pk[Long] = NotAssigned, nodeId: Long, themeId: Long, description: Option[String], note: Option[String], choice: Choice)

  val withChoice = {
    get[Pk[Long]]("question.id") ~
      get[Long]("question.node_id") ~
      get[Long]("question.theme_id") ~
      get[Option[String]]("question.description") ~
      get[Option[String]]("question.note") ~
      Choice.simple map {
      case id ~ nodeId ~ themeId ~ description ~note ~ choice => QuestionChoice(id, nodeId, themeId, description,note,choice)
    }
  }


  def list: Seq[Question] = {
    val choices: Seq[QuestionChoice] =
      DB.withConnection {
        implicit c =>
          SQL(
            """
              select *
              from question,theme,choice
              where question.theme_id = theme.id
              and choice.question_id = question.id
            """
          ).as(withChoice *)
      }

    val questions = choices.groupBy(c => (c.id, c.nodeId,c.themeId, c.description,c.note)) map {
      case (k, v) => Question(k._1, k._2, k._3, k._4,k._5,v.map(_.choice))
    }
    questions.toSeq
  }


  def findById(id: Long): Option[Question] = {
    DB.withConnection {
      implicit c =>
        val choices: Seq[QuestionChoice] =
          DB.withConnection {
            implicit c =>
              SQL(
                """
                  select *
                  from question,theme,choice
                  where question.theme_id = theme.id
                  and choice.question_id = question.id
                  and question.id = {id}
                """
              ).on('id -> id).as(withChoice *)
          }
        choices.headOption.map {
          c: QuestionChoice =>
            Question(c.id, c.nodeId,c.themeId, c.description,c.note,choices.map(_.choice))
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


case class QuestionDetail(id: Pk[Long] = NotAssigned, desc: Option[String], note: Option[String],  theme: Theme, choices: Seq[Choice] = Nil) {
  import libs.json.Json

  def toJson() = Json.toJson(Map(
    "id" -> Json.toJson(id.get),
    "desc" -> Json.toJson(desc.getOrElse("--")),
    "note" -> Json.toJson(note.getOrElse("")),
    "choices" -> Json.toJson(
      choices.map{ c =>
        Json.toJson(Map(
          "title" -> Json.toJson(c.title),
          "id" -> Json.toJson(c.id.get),
          "correct" -> Json.toJson(c.correct.getOrElse(false)),
          "note" -> Json.toJson(c.note.getOrElse("")),
          "questionId" -> Json.toJson(c.questionId)
        ))
      }
    )
  ))

}

object QuestionDetail {

  case class ChoiceDetail(id: Pk[Long] = NotAssigned, desc: Option[String],note: Option[String], theme: Theme, choice: Choice)

  val complex = {
    get[Pk[Long]]("question.id") ~
      get[Option[String]]("question.description") ~
      get[Option[String]]("question.note") ~
      Theme.simple ~
      Choice.simple map {
      case id ~ desc ~ note~ theme ~ choice => ChoiceDetail(id, desc,note, theme, choice)
    }
  }

  def findById(id: Long): Option[QuestionDetail] = {
    DB.withConnection {
      implicit c =>
        val choices: Seq[ChoiceDetail] =
          DB.withConnection {
            implicit c =>
              SQL(
                """
                  select *
                  from question, theme,node,choice
                  where question.theme_id = theme.id
                  and choice.question_id = question.id
                  and choice.node_id = node.id
                  and question.id = {id}
                """
              ).on('id -> id).as(complex *)
          }
        choices.headOption.map {
          c: ChoiceDetail =>
            QuestionDetail(c.id, c.desc,c.note, c.theme, choices.map(_.choice))
        }
    }
  }

}
