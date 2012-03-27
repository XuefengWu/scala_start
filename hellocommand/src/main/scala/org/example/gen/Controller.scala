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
  def gen(model: String)(implicit baseDir: String) {
    def genForm(implicit out: FileWriter) {
      out.write("""
    val %sForm = Form(
      mapping(
        "id" -> ignored(NotAssigned:Pk[Long]),
        "title" -> nonEmptyText
      )(%s.apply)(%s.unapply)
    )
      """.format(model, model.capitalize, model.capitalize))
    }
    def genActions(implicit out: FileWriter) {

      out.write("""
    def list = Action {
      Ok(views.html.%s.list(%s.list(),%sForm))
    }
      """.format(model, model.capitalize, model))

      out.write("""
      def save = Action { implicit request =>
        %sForm.bindFromRequest.fold(
          errors => BadRequest(views.html.%s.list(%s.list(),errors)),
          v => {
            %s.create(v)
            Redirect(routes.%ss.list)
          }
        )
      }
      """.format( model, model,model.capitalize, model.capitalize, model.capitalize))

      out.write("""
      def delete(id:Long) = Action {
      %s.delete(id)
      Redirect(routes.%ss.list)
    }
    """.format(model.capitalize, model.capitalize))

      out.write("""
    def edit(id: Long) = Action {
      %s.findById(id).map { v =>
        Ok(views.html.%s.edit(id, %sForm.fill(v)))
      }.getOrElse(NotFound)
    }
     """.format(model.capitalize, model, model))


      out.write("""
    def update(id: Long) = Action { implicit request =>
      %sForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.%s.edit(id, formWithErrors)),
        v => {
          %s.update(id, v)
          Redirect(routes.%ss.list).flashing("success" -> "%%s has been updated".format(v.title))
        }
      )
    }
     """.format(model,model,model.capitalize,model.capitalize))


      out.write("""
    def create = Action {
      Ok(views.html.%s.create(%sForm))
    }
     """.format(model,model))

    }

    //check app/controllers folder
    val evDir = new File(baseDir + "/app/controllers")

    if (!evDir.exists())
      evDir.mkdir()
    //create [model]s.scala
    val cFile = new File(baseDir + "/app/controllers/%ss.scala".format(model.capitalize))
    if (cFile.exists())
      System.out.print(model + " is alread exists")

    implicit val out: FileWriter = new FileWriter(cFile)
    out.write("""
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import anorm.{Pk, NotAssigned}
import models.%s

object %ss extends Controller {
    """.format(model.capitalize, model.capitalize))


    genForm

    genActions

    out.write("}")
    out.close()
  }

}
