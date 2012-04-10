
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.Comment

object Comments extends Controller {
    val commentForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "node" -> longNumber,
	"replyTo" -> optional(longNumber)
    )(Comment.apply)(Comment.unapply)
  )
    
      /**
   * This result directly redirect to the Comment home.
   */
  val Home = Redirect(routes.Comments.list(0, 2, ""))
    
    def list(page: Int, orderBy: Int, filter: String) = Action {  implicit request =>
      Ok(views.html.comment.list(Comment.list(page = page,orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter))
    }
      
      def save = Action { implicit request =>
        commentForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.comment.create(formWithErrors)),
          v => {
            Comment.create(v)
            Home
          }
        )
      }
      
      def delete(id:Long) = Action {   implicit request =>
      Comment.delete(id)
      Home
    }
    
    def edit(id: Long) = Action {  implicit request =>
      Comment.findById(id).map { v =>
        Ok(views.html.comment.edit(id, commentForm.fill(v)))
      }.getOrElse(NotFound)
    }
     
    def update(id: Long) = Action { implicit request =>
      commentForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.comment.edit(id, formWithErrors)),
        v => {
          Comment.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
    }
     
    def create = Action {  implicit request =>
      Ok(views.html.comment.create(commentForm))
    }
     }