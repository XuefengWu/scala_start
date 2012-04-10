
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.Theme

object Themes extends Controller {
    val themeForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText
    )(Theme.apply)(Theme.unapply)
  )
    
      /**
   * This result directly redirect to the Theme home.
   */
  val Home = Redirect(routes.Themes.list(0, 2, ""))
    
    def list(page: Int, orderBy: Int, filter: String) = Action {  implicit request =>
      Ok(views.html.theme.list(Theme.list(page = page,orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter))
    }
      
      def save = Action { implicit request =>
        themeForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.theme.create(formWithErrors)),
          v => {
            Theme.create(v)
            Home
          }
        )
      }
      
      def delete(id:Long) = Action {   implicit request =>
      Theme.delete(id)
      Home
    }
    
    def edit(id: Long) = Action {  implicit request =>
      Theme.findById(id).map { v =>
        Ok(views.html.theme.edit(id, themeForm.fill(v)))
      }.getOrElse(NotFound)
    }
     
    def update(id: Long) = Action { implicit request =>
      themeForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.theme.edit(id, formWithErrors)),
        v => {
          Theme.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
    }
     
    def create = Action {  implicit request =>
      Ok(views.html.theme.create(themeForm))
    }
     }