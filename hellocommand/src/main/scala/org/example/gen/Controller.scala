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
  def gen(model:String)(implicit baseDir:String){
    def genForm(implicit out:FileWriter){
      out.write("""
    val %sForm = Form(
      "title" -> nonEmptyText
    )
      """.format(model))
    }
    def genActions(implicit out:FileWriter){

      out.write("""
    def %ss = Action {
      Ok(views.html.%s.list(%s.list(),%sForm))
    }
      """.format(model,model,model.capitalize,model))

      out.write("""
      def new%s = Action { implicit request =>
        %sForm.bindFromRequest.fold(
          errors => BadRequest(views.html.%s.list(%s.list(),errors)),
          title => {
            %s.create(title)
            Redirect(routes.%ss.%ss)
          }
        )
      }
      """.format(model.capitalize,model,model,model.capitalize,model.capitalize,model.capitalize,model))

      out.write("""
      def delete%s(id:Long) = Action {
      %s.delete(id)
      Redirect(routes.%ss.%ss)
    }
    """.format(model.capitalize,model.capitalize,model.capitalize,model))
    }
    //check app/controllers folder
    val evDir = new File(baseDir+"/app/controllers")

    if(!evDir.exists())
      evDir.mkdir()
    //create [model]s.scala
    val cFile = new File(baseDir+"/app/controllers/%ss.scala".format(model.capitalize))
    if(cFile.exists())
      System.out.print(model + " is alread exists")

    implicit val out:FileWriter = new FileWriter(cFile)
    out.write("""
package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._
import models.%s

object %ss extends Controller {
    """.format(model.capitalize,model.capitalize))


    genForm

    genActions

    out.write("}")
    out.close()
  }

}
