package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import play.api.libs


case class Question(id: Pk[Long] = NotAssigned, nodeId: Long, themeId: Long, description: Option[String], note: Option[String])

object Question {
  val simple = {
    get[Pk[Long]]("question.id") ~
      get[Long]("question.node_id") ~
      get[Long]("question.theme_id") ~
      get[Option[String]]("question.description") ~
      get[Option[String]]("question.note")map {
      case id ~ nodeId ~ themeId ~ description ~note => Question(id, nodeId, themeId, description,note)
    }
  }

  /**
   * Parse a (Question,Node,Theme) from a ResultSet
   */
  val withReference = Question.simple ~ Node.simple ~ Theme.simple map {
    case question ~ node ~ theme => (question, node, theme)
  }
  /**
   * Return a page of (Question,Node,Theme).
   *
   * @param page Page to display
   * @param pageSize Number of books per page
   * @param orderBy used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Question, Node, Theme)] = {

    val offest = pageSize * page

    DB.withConnection {
      implicit connection =>

        val questions = SQL(
          """
            select * from question
            left join Node on question.node_id = node.id
            left join Theme on question.theme_id = theme.id
            where (question.description like {filter})
            order by {orderBy} nulls last
            limit {pageSize} offset {offset}
          """
        ).on(
          'pageSize -> pageSize,
          'offset -> offest,
          'filter -> filter,
          'orderBy -> orderBy
        ).as(Question.withReference *)

        val totalRows = SQL(
          """
            select count(*) from question
            left join Node on question.node_id = node.id
            left join Theme on question.theme_id = theme.id
            where  (question.description like {filter})
          """
        ).on(
          'filter -> filter
        ).as(scalar[Long].single)

        Page(questions, page, offest, totalRows)

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


  def findById(id: Long): Option[Question] = {
    DB.withConnection {
      implicit c =>
        SQL("select * from question where  id = {id}").on('id -> id).as(simple.singleOpt)
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


  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = DB.withConnection {
    implicit connection =>
      SQL("select * from question").as(Question.simple *).map(c => c.id.toString -> c.toString)
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
          "question" -> Json.toJson(c.questionId)
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
            QuestionDetail(c.id, c.desc,c.note, c.theme, List(c.choice))
        }
    }
  }

  def list: Seq[QuestionDetail] = {
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
            """
          ).as(complex *)
      }

    val questions = choices.groupBy(c => (c.id, c.desc,c.note, c.theme)) map {
      case (k, v) => QuestionDetail(k._1, k._2, k._3, k._4,v.map(_.choice))
    }
    questions.toSeq
  }
}
