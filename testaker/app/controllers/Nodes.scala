
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.Node

object Nodes extends Controller {
    val nodeForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "title" -> nonEmptyText
    )(Node.apply)(Node.unapply)
  )
    
      /**
   * This result directly redirect to the Node home.
   */
  val Home = Redirect(routes.Nodes.list(0, 2, ""))
    
    def list(page: Int, orderBy: Int, filter: String) = Action {  implicit request =>
      Ok(views.html.node.list(Node.list(page = page,orderBy = orderBy, filter = ("%"+filter+"%")),orderBy, filter))
    }
      
      def save = Action { implicit request =>
        nodeForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.node.create(formWithErrors)),
          v => {
            Node.create(v)
            Home
          }
        )
      }
      
      def delete(id:Long) = Action {   implicit request =>
      Node.delete(id)
      Home
    }
    
    def edit(id: Long) = Action {  implicit request =>
      Node.findById(id).map { v =>
        Ok(views.html.node.edit(id, nodeForm.fill(v)))
      }.getOrElse(NotFound)
    }
     
    def update(id: Long) = Action { implicit request =>
      nodeForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.node.edit(id, formWithErrors)),
        v => {
          Node.update(id, v)
          Home.flashing("success" -> "%s has been updated".format(v))
        }
      )
    }
     
    def create = Action {  implicit request =>
      Ok(views.html.node.create(nodeForm))
    }
     }