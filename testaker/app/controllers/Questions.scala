
package controllers

import play.api._
import play.api.mvc._
import libs.json.Json

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.{QuestionDetail, Question}

object Questions extends Controller {
    val questionForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "node" -> longNumber,
	  "theme" -> longNumber,
	  "description" -> optional(text),
      "note" -> optional(text)
    )(Question.apply)(Question.unapply)
  )
    
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
    //Ok(views.html.question.list(Question.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")), orderBy, filter))
      Ok(Json.toJson(
        QuestionDetail.list.map{ q =>
          q.toJson()
        }))
  }

  def show(id: Long) = Action {
    implicit request =>
      QuestionDetail.findById(id).map {
        q =>
          Ok(q.toJson())
      }.getOrElse(NotFound)
  }

      def save = Action { implicit request =>
        questionForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.question.create(formWithErrors)),
          v => {
            Question.create(v)
            Home
          }
        )
      }
      
      def delete(id:Long) = Action {   implicit request =>
      Question.delete(id)
      Home
    }
    
    def edit(id: Long) = Action {  implicit request =>
      Question.findById(id).map { v =>
        Ok(views.html.question.edit(id, questionForm.fill(v)))
      }.getOrElse(NotFound)
    }
     
    def update(id: Long) = Action { implicit request =>
      questionForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.question.edit(id, formWithErrors)),
        v => {
          Question.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
    }
     
    def create = Action {  implicit request =>
      Ok(views.html.question.create(questionForm))
    }
     }