package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.Answer

object Answers extends Controller {

  val answerForm = Form(
    mapping(
      "id" -> ignored(NotAssigned: Pk[Long]),
      "node" -> longNumber,
      "question" -> longNumber,
      "choice" -> longNumber,
      "testaker" -> longNumber
    )(Answer.apply)(Answer.unapply)
  )

  /**
   * This result directly redirect to the Answer home.
   */
  val Home = Redirect(routes.Answers.list(0, 2, ""))

  def list(page: Int, orderBy: Int, filter: String) = Action {
    implicit request =>
      Ok(views.html.answer.list(Answer.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")), orderBy, filter))
  }

  def save = Action {
    implicit request =>
      answerForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.answer.create(formWithErrors)),
        v => {
          Answer.create(v)
          Home
        }
      )
  }

  def delete(id: Long) = Action {
    implicit request =>
      Answer.delete(id)
      Home
  }

  def edit(id: Long) = Action {
    implicit request =>
      Answer.findById(id).map {
        v =>
          Ok(views.html.answer.edit(id, answerForm.fill(v)))
      }.getOrElse(NotFound)
  }

  def update(id: Long) = Action {
    implicit request =>
      answerForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.answer.edit(id, formWithErrors)),
        v => {
          Answer.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
  }

  def create = Action {
    implicit request =>
      Ok(views.html.answer.create(answerForm))
  }
}