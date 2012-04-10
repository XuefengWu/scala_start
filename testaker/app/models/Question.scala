package models

import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
import anorm._


case class Question(id: Pk[Long] = NotAssigned, nodeId: Long, themeId: Long, desc: Option[String])

object Question {
  val simple = {
    get[Pk[Long]]("question.id") ~
      get[Long]("question.node_id") ~
      get[Long]("question.theme_id") ~
      get[Option[String]]("question.desc") map {
      case id ~ nodeId ~ themeId ~ desc => Question(id, nodeId, themeId, desc)
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
            where (question.desc like {filter})
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
            where  (question.desc like {filter})
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
        SQL("insert into question (id,node_id,theme_id,desc) values ((select next value for question_id_seq),{node_id},{theme_id},{desc})").on(
          'node_id -> v.nodeId,
          'theme_id -> v.themeId,
          'desc -> v.desc
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
          "update question set node_id = {node_id},theme_id = {theme_id},desc = {desc} where id = {id}"
        ).on(
          'id -> id,
          'node_id -> v.nodeId,
          'theme_id -> v.themeId,
          'desc -> v.desc
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

case class QuestionDetail(id: Pk[Long] = NotAssigned, desc: Option[String],title:String,node: Node, theme: Theme){
  def choices: Seq[Choice] = {
    DB.withConnection {
      implicit c =>
        SQL(
          """
            select * from choice
            left join Node on choice.node_id = node.id
            left join Question on choice.question_id = question.id
            where question.id = {id}
          """
        ).on('id -> id).as(Choice.simple *)
    }
  }
}

object QuestionDetail {
  val simple = {
      get[Pk[Long]]("question.id") ~
      get[Option[String]]("question.desc") ~
      get[String]("node.title") ~
      Node.simple ~
      Theme.simple map {
      case id ~ desc ~ title ~ node ~ theme => QuestionDetail(id, desc, title, node, theme)
    }
  }

  def findById(id: Long): Option[QuestionDetail] = {
    DB.withConnection {
      implicit c =>
        SQL(
          """
            select * from question
            left join node on question.node_id = node.id
            left join theme on question.theme_id = theme.id
            where question.id = {id}
          """
        ).on('id -> id).as(simple.singleOpt)
    }
  }

  def list:Seq[QuestionDetail] = {
    DB.withConnection {
      implicit c =>
        SQL(
          """
            select * from question
            left join node on question.node_id = node.id
            left join theme on question.theme_id = theme.id
          """
        ).as(simple *)
    }
  }
}