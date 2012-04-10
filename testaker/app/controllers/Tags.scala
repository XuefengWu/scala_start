
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.Tag

object Tags extends Controller {
    val tagForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "name" -> nonEmptyText,
	"theme" -> longNumber
    )(Tag.apply)(Tag.unapply)
  )
    
      /**
   * This result directly redirect to the Tag home.
   */
  val Home = Redirect(routes.Tags.list(0, 2, ""))
    
    def list(page: Int, orderBy: Int, filter: String) = Action {  implicit request =>
      Ok(views.html.tag.list(Tag.list(page = page,orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter))
    }
      
      def save = Action { implicit request =>
        tagForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.tag.create(formWithErrors)),
          v => {
            Tag.create(v)
            Home
          }
        )
      }
      
      def delete(id:Long) = Action {   implicit request =>
      Tag.delete(id)
      Home
    }
    
    def edit(id: Long) = Action {  implicit request =>
      Tag.findById(id).map { v =>
        Ok(views.html.tag.edit(id, tagForm.fill(v)))
      }.getOrElse(NotFound)
    }
     
    def update(id: Long) = Action { implicit request =>
      tagForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.tag.edit(id, formWithErrors)),
        v => {
          Tag.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
    }
     
    def create = Action {  implicit request =>
      Ok(views.html.tag.create(tagForm))
    }
     }