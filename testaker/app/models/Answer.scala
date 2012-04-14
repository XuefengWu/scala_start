package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


case class Answer(id: Pk[Long] = NotAssigned, nodeId: Long = 0.toLong, questionId: Long, choiceId: Option[Long], examId: Long,note:Option[String]) {
  def create() = {
    Answer.create(this)
  }

  def update(){
    Answer.update(id.get,this)
  }
}

object Answer {
  val simple = {
    get[Pk[Long]]("answer.id") ~
      get[Long]("answer.node_id") ~
      get[Long]("answer.question_id") ~
      get[Option[Long]]("answer.choice_id") ~
      get[Long]("answer.exam_id")~
      get[Option[String]]("answer.note")map {
      case id ~ nodeId ~ questionId ~ choiceId ~ examId~note => Answer(id, nodeId, questionId, choiceId, examId,note)
    }
  }

  /**
   * Parse a (Answer,Node,Question,Choice,Testaker) from a ResultSet
   */
  val withReference = Answer.simple ~ Node.simple ~ Choice.simple ~ Exam.simple map {
    case answer ~ node ~ choice ~ exam => (answer, node, choice, exam)
  }
  /**
   * Return a page of (Answer,Node,Question,Choice,Testaker).
   *
   * @param page Page to display
   * @param pageSize Number of books per page
   * @param orderBy used for sorting
   * @param filter Filter applied on the name column
   */
  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%"): Page[(Answer, Node, Choice, Exam)] = {

    val offest = pageSize * page

    DB.withConnection {
      implicit connection =>

        val answers = SQL(
          """
            select * from answer
            left join Node on answer.node_id = node.id
            left join Question on answer.question_id = question.id
            left join Choice on answer.choice_id = choice.id
            left join Exam on answer.exam_id = exam.id
            where  1 = 1
            order by {orderBy} nulls last
            limit {pageSize} offset {offset}
          """
        ).on(
          'pageSize -> pageSize,
          'offset -> offest,
          'filter -> filter,
          'orderBy -> orderBy
        ).as(Answer.withReference *)

        val totalRows = SQL(
          """
            select count(*) from answer
            left join Node on answer.node_id = node.id
            left join Question on answer.question_id = question.id
            left join Choice on answer.choice_id = choice.id
            left join Exam on answer.exam_id = exam.id
            where   1 = 1
          """
        ).on(
          'filter -> filter
        ).as(scalar[Long].single)

        Page(answers, page, offest, totalRows)

    }

  }

  def findAnswerByQuestion(questionId: Long, examId: Long) = DB.withConnection {
    implicit connection =>
      SQL("select * from answer where question_id = {question_id} and exam_id = {exam_id}").on(
        'question_id -> questionId,
        'exam_id -> examId
      ).as(simple.singleOpt)
  }
  
  def getAnswerByQuestion(questionId: Long, examId: Long):Answer = {
    findAnswerByQuestion(questionId, examId).getOrElse{
      val id = Answer(NotAssigned, 0.toLong,questionId,None,examId,None).create()
      findById(id).get
    }

  }

  def create(v: Answer):Long = {
    DB.withConnection {
      implicit connection =>
        findAnswerByQuestion(v.questionId, v.examId).map{
          a => a.id.get
        }.getOrElse{
          val nodeId = Node().create()
          SQL("insert into answer (node_id,question_id,choice_id,exam_id,note) values ({node_id},{question_id},{choice_id},{exam_id},{note})").on(
            'node_id -> nodeId,
            'question_id -> v.questionId,
            'choice_id -> v.choiceId,
            'exam_id -> v.examId,
            'note -> v.note
          ).executeUpdate()
          val id:Long = SQL("SELECT LAST_INSERT_ID()").as(scalar[Long].single)
          id
        }
    }
  }


  def findById(id: Long): Option[Answer] = {
    DB.withConnection {
      implicit c =>
        SQL("select * from answer where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }


  def delete(id: Long) {
    DB.withConnection {
      implicit c =>
        SQL("delete from answer where id = {id}").on(
          'id -> id
        ).executeUpdate()
    }
  }


  def update(id: Long, v: Answer) = {
    DB.withConnection {
      implicit connection =>
        SQL(
          "update answer set node_id = {node_id},question_id = {question_id},choice_id = {choice_id},exam_id = {exam_id},note ={note} where id = {id}"
        ).on(
          'id -> id,
          'node_id -> v.nodeId,
          'question_id -> v.questionId,
          'choice_id -> v.choiceId,
          'note -> v.note,
          'exam_id -> v.examId
        ).executeUpdate()
    }
  }

  
  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = DB.withConnection {
    implicit connection =>
      SQL("select * from answer").as(Answer.simple *).map(c => c.id.toString -> c.toString)
  }
}