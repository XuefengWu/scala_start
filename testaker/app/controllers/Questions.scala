package controllers

import play.api._
import play.api.mvc._
import libs.json.Json

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.{Comment, Question}

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

  def questionComment(id:Long) = Action {
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