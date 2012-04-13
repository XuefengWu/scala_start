package controllers

import play.api._
import play.api.mvc._
import libs.json.Json

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.{Answer, Node, Comment, Question}

object Questions extends Controller {

  /**
   * This result directly redirect to the Question home.
   */
  val Home = Redirect(routes.Questions.list(0, 2, ""))

  def index = Action {
    implicit request =>
      Ok(views.html.index())
  }

  def list(page: Int, orderBy: Int, filter: String) = Action {
    implicit request =>
      Ok(Json.toJson(
        Question.list.map {
          q =>
            q.toJson()
        }))
  }

  def show(id: Long) = Action {
    implicit request =>
      Question.findById(id).map {
        q =>
          Ok(q.toJson())
      }.getOrElse(NotFound)
  }

  def answer = Action(parse.json) {
    request =>
      val body = request.body
      val questionId = (body \ "questionId").as[Long]
      val choiceId = (body \ "choiceId").as[Long]
      val examId = (body \ "examId").as[Long]

      (body \ "note").asOpt[String].map {
        note =>
          Answer.findAnswerByQuestion(questionId, examId).map {
            a =>
              if(a.choiceId == choiceId)
                a.copy(note = Some(note)).update
              else println("sorry, your answer choice is %d".format(choiceId))
          }.getOrElse{
            println("can not find anser questionId:%d,examId:%d".format(questionId,examId))
          }
          //save note to answer
          Ok("save note to answer")
      }.getOrElse {
        //add answer

        //Answer(id:Pk[Long] = NotAssigned, nodeId:Long,questionId:Long,choiceId:Long,examId:Long)
        Answer(NotAssigned, 0.toLong, questionId, choiceId, examId, None).create()
        Ok("add new answer")
      }
  }

  def comment = Action(parse.json) {
    request =>
      val body = request.body
      val qNodeId = (body \ "qNodeId").as[Long]
      val context = (body \ "context").as[String]
      Comment.create(qNodeId,context).map{
        c => Ok(c.toJson())
      }.getOrElse{
        Ok(Json.toJson(Map("error" -> "error")))
      }      
  }
  
  def questionComment(id: Long) = Action {
    implicit request =>
      Ok(Json.toJson(
        Comment.forQuestion(id).map {
          qc =>
            qc.toJson()
        }))
  }

  def delete(id: Long) = Action {
    implicit request =>
      Question.delete(id)
      Home
  }

}