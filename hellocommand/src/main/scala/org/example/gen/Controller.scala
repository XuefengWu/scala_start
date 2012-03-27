package org.example.gen

import java.io.{File, FileWriter}

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */

object Controller {
  def gen(model: String)={

    val result = new StringBuffer()
    result.append(genControllerHead(model))

    result.append(genForm(model))

    result.append(genActions(model))

    result.append(genControllerEnd)
    result.toString
  }

  def genControllerEnd = "}"

  def genForm(model: String)={

    """val %sForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      "title" -> nonEmptyText
    )(%s.apply)(%s.unapply)
  )
    """.format(model, model.capitalize, model.capitalize)
  }

  def genControllerHead(model:String) = """
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.%s

object %ss extends Controller {
    """.format(model.capitalize, model.capitalize)


  def genActions(model: String)= {

    def genList = """
    def list = Action {
      Ok(views.html.%s.list(%s.list(),%sForm))
    }
      """.format(model, model.capitalize, model)

    def genSave = """
      def save = Action { implicit request =>
        %sForm.bindFromRequest.fold(
          errors => BadRequest(views.html.%s.list(%s.list(),errors)),
          v => {
            %s.create(v)
            Redirect(routes.%ss.list)
          }
        )
      }
      """.format( model, model,model.capitalize, model.capitalize, model.capitalize)

    def genDelete = """
      def delete(id:Long) = Action {
      %s.delete(id)
      Redirect(routes.%ss.list)
    }
    """.format(model.capitalize, model.capitalize)

    def genEdit ="""
    def edit(id: Long) = Action {
      %s.findById(id).map { v =>
        Ok(views.html.%s.edit(id, %sForm.fill(v)))
      }.getOrElse(NotFound)
    }
     """.format(model.capitalize, model, model)


    def genUpdate = """
    def update(id: Long) = Action { implicit request =>
      %sForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.%s.edit(id, formWithErrors)),
        v => {
          %s.update(id, v)
          Redirect(routes.%ss.list).flashing("success" -> "%%s has been updated".format(v.title))
        }
      )
    }
     """.format(model,model,model.capitalize,model.capitalize)

    def genCreate = """
    def create = Action {
      Ok(views.html.%s.create(%sForm))
    }
     """.format(model,model)

    val result = new StringBuffer()

    result.append(genList)

    result.append(genSave)

    result.append(genDelete)

    result.append(genEdit)

    result.append(genUpdate)

    result.append(genCreate)

    result.toString
  }

}
