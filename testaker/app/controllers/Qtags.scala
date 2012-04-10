
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.Qtag

object Qtags extends Controller {
    val qtagForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "question" -> longNumber,
	"tag" -> longNumber
    )(Qtag.apply)(Qtag.unapply)
  )
    
      /**
   * This result directly redirect to the Qtag home.
   */
  val Home = Redirect(routes.Qtags.list(0, 2, ""))
    
    def list(page: Int, orderBy: Int, filter: String) = Action {  implicit request =>
      Ok(views.html.qtag.list(Qtag.list(page = page,orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter))
    }
      
      def save = Action { implicit request =>
        qtagForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.qtag.create(formWithErrors)),
          v => {
            Qtag.create(v)
            Home
          }
        )
      }
      
      def delete(id:Long) = Action {   implicit request =>
      Qtag.delete(id)
      Home
    }
    
    def edit(id: Long) = Action {  implicit request =>
      Qtag.findById(id).map { v =>
        Ok(views.html.qtag.edit(id, qtagForm.fill(v)))
      }.getOrElse(NotFound)
    }
     
    def update(id: Long) = Action { implicit request =>
      qtagForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.qtag.edit(id, formWithErrors)),
        v => {
          Qtag.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
    }
     
    def create = Action {  implicit request =>
      Ok(views.html.qtag.create(qtagForm))
    }
     }