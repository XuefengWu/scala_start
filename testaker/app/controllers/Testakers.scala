
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.Testaker

object Testakers extends Controller {
    val testakerForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText
    )(Testaker.apply)(Testaker.unapply)
  )
    
      /**
   * This result directly redirect to the Testaker home.
   */
  val Home = Redirect(routes.Testakers.list(0, 2, ""))
    
    def list(page: Int, orderBy: Int, filter: String) = Action {  implicit request =>
      Ok(views.html.testaker.list(Testaker.list(page = page,orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter))
    }
      
      def save = Action { implicit request =>
        testakerForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.testaker.create(formWithErrors)),
          v => {
            Testaker.create(v)
            Home
          }
        )
      }
      
      def delete(id:Long) = Action {   implicit request =>
      Testaker.delete(id)
      Home
    }
    
    def edit(id: Long) = Action {  implicit request =>
      Testaker.findById(id).map { v =>
        Ok(views.html.testaker.edit(id, testakerForm.fill(v)))
      }.getOrElse(NotFound)
    }
     
    def update(id: Long) = Action { implicit request =>
      testakerForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.testaker.edit(id, formWithErrors)),
        v => {
          Testaker.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
    }
     
    def create = Action {  implicit request =>
      Ok(views.html.testaker.create(testakerForm))
    }
     }