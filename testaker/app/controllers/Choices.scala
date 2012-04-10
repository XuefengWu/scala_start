
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.Choice

object Choices extends Controller {
    val choiceForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "title" -> text,
      "node" -> longNumber,
	"question" -> longNumber,
	"correct" -> optional(boolean)
    )(Choice.apply)(Choice.unapply)
  )
    
      /**
   * This result directly redirect to the Choice home.
   */
  val Home = Redirect(routes.Choices.list(0, 2, ""))
    
    def list(page: Int, orderBy: Int, filter: String) = Action {  implicit request =>
      Ok(views.html.choice.list(Choice.list(page = page,orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter))
    }
      
      def save = Action { implicit request =>
        choiceForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.choice.create(formWithErrors)),
          v => {
            Choice.create(v)
            Home
          }
        )
      }
      
      def delete(id:Long) = Action {   implicit request =>
      Choice.delete(id)
      Home
    }
    
    def edit(id: Long) = Action {  implicit request =>
      Choice.findById(id).map { v =>
        Ok(views.html.choice.edit(id, choiceForm.fill(v)))
      }.getOrElse(NotFound)
    }
     
    def update(id: Long) = Action { implicit request =>
      choiceForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.choice.edit(id, formWithErrors)),
        v => {
          Choice.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
    }
     
    def create = Action {  implicit request =>
      Ok(views.html.choice.create(choiceForm))
    }
     }