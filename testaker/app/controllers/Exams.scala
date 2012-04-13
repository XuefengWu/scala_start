
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.Exam

object Exams extends Controller {
    val examForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText,
	"testaker" -> longNumber,
      "theme" -> longNumber
    )(Exam.apply)(Exam.unapply)
  )
    
      /**
   * This result directly redirect to the Exam home.
   */
  val Home = Redirect(routes.Exams.list(0, 2, ""))
    
    def list(page: Int, orderBy: Int, filter: String) = Action {  implicit request =>
      Ok(views.html.exam.list(Exam.list(page = page,orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter))
    }
      
      def save = Action { implicit request =>
        examForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.exam.create(formWithErrors)),
          v => {
            Exam.create(v)
            Home
          }
        )
      }
      
      def delete(id:Long) = Action {   implicit request =>
      Exam.delete(id)
      Home
    }
    
    def edit(id: Long) = Action {  implicit request =>
      Exam.findById(id).map { v =>
        Ok(views.html.exam.edit(id, examForm.fill(v)))
      }.getOrElse(NotFound)
    }
     
    def update(id: Long) = Action { implicit request =>
      examForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.exam.edit(id, formWithErrors)),
        v => {
          Exam.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
    }
     
    def create = Action {  implicit request =>
      Ok(views.html.exam.create(examForm))
    }
     }