package org.example.gen

import java.io.{File, FileWriter}
import org.example.ScaffoldPlugin

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */

object Controller {
  def gen(model: String,fields:Seq[(String, String)])={

    val result = new StringBuffer()
    result.append(genControllerHead(model))

    result.append(genForm(model,fields))

    result.append(genActions(model))

    result.append(genControllerEnd)
    result.toString
  }

  def genControllerEnd = "}"

  def genForm(model: String,fields:Seq[(String, String)])={
    val mapping = fields.map{f => 
      f._2 match {
        case t if ScaffoldPlugin.isModelType(t) => {
          if(f._2.contains("Required")) {
            "\"%s\" -> longNumber".format(f._1)
          }else{
            "\"%s\" -> optional(longNumber)".format(f._1)
          }
        }
        case "String" => "\"%s\" -> optional(text)".format(f._1)
        case "Option[String]" => "\"%s\" -> optional(text)".format(f._1)
        case "Required[String]" => "\"%s\" -> nonEmptyText".format(f._1)
        case "Long" => "\"%s\" -> optional(longNumber)".format(f._1)
        case "Option[Long]" => "\"%s\" -> optional(longNumber)".format(f._1)
        case "Required[Long]" => "\"%s\" -> longNumber".format(f._1)
        case "Integer" => "\"%s\" -> optional(number)".format(f._1)
        case "Option[Integer]" => "\"%s\" -> optional(number)".format(f._1)
        case "Required[Integer]" => "\"%s\" -> number".format(f._1)
        case "Boolean" => "\"%s\" -> optional(boolean)".format(f._1)
        case "Option[Boolean]" => "\"%s\" -> optional(boolean)".format(f._1)
        case "Required[Boolean]" => "\"%s\" -> boolean".format(f._1)
        case "BigInteger" => "\"%s\" -> optional(longNumber)".format(f._1)
        case "Option[BigInteger]" => "\"%s\" -> optional(longNumber)".format(f._1)
        case "Required[BigInteger]" => "\"%s\" -> longNumber".format(f._1)
        case "Float" => "\"%s\" -> optional(number)".format(f._1)
        case "Option[Float]" => "\"%s\" -> optional(number)".format(f._1)
        case "Required[Float]" => "\"%s\" -> number".format(f._1)
        case "Double" => "\"%s\" -> optional(number)".format(f._1)
        case "Option[Double]" => "\"%s\" -> optional(number)".format(f._1)
        case "Required[Double]" => "\"%s\" -> number".format(f._1)
        case "BigDecimal" => "\"%s\" -> optional(number)".format(f._1)
        case "Option[BigDecimal]" => "\"%s\" -> optional(number)".format(f._1)
        case "Required[BigDecimal]" => "\"%s\" -> number".format(f._1)
        case "Time" => "\"%s\" -> optional(date(\"HH:mm:ss\"))".format(f._1)
        case "Option[Time]" => "\"%s\" -> optional(date(\"HH:mm:ss\"))".format(f._1)
        case "Required[Time]" => "\"%s\" -> date(\"HH:mm:ss\")".format(f._1)
        case "Timestamp" => "\"%s\" -> optional(date(\"yyyy-MM-dd HH:mm:ss\"))".format(f._1)
        case "Option[Timestamp]" => "\"%s\" -> optional(date(\"yyyy-MM-dd HH:mm:ss\"))".format(f._1)
        case "Required[Timestamp]" => "\"%s\" -> date(\"yyyy-MM-dd HH:mm:ss\")".format(f._1)
        case "Date" => "\"%s\" -> optional(date(\"yyyy-MM-dd\"))".format(f._1)
        case "Option[Date]" => "\"%s\" -> optional(date(\"yyyy-MM-dd\"))".format(f._1)
        case "Required[Date]" => "\"%s\" -> date(\"yyyy-MM-dd\")".format(f._1)
        case _ => "\"%s\" -> nonEmptyText".format(f._1)
      }

    }.mkString(",\n\t")

    """val %sForm = Form(
    mapping(
      "id" -> ignored(NotAssigned:Pk[Long]),
      %s
    )(%s.apply)(%s.unapply)
  )
    """.format(model,mapping, model.capitalize, model.capitalize)
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
    def list(page: Int, orderBy: Int, filter: String) = Action {  implicit request =>
      Ok(views.html.%s.list(%s.list(page = page,orderBy = orderBy, filter = ("%%"+filter+"%%")),orderBy, filter))
    }
      """.format(model, model.capitalize, model)

    def genSave = """
      def save = Action { implicit request =>
        %sForm.bindFromRequest.fold(
          formWithErrors => BadRequest(views.html.%s.create(formWithErrors)),
          v => {
            %s.create(v)
            Home
          }
        )
      }
      """.format( model, model, model.capitalize, model.capitalize)

    def genDelete = """
      def delete(id:Long) = Action {   implicit request =>
      %s.delete(id)
      Home
    }
    """.format(model.capitalize, model.capitalize)

    def genEdit ="""
    def edit(id: Long) = Action {  implicit request =>
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
          Home.flashing("success" -> "%%s has been updated".format(v))
        }
      )
    }
     """.format(model,model,model.capitalize,model.capitalize)

    def genCreate = """
    def create = Action {  implicit request =>
      Ok(views.html.%s.create(%sForm))
    }
     """.format(model,model)

    def genModelHome = """
      /**
   * This result directly redirect to the %s home.
   */
  val Home = Redirect(routes.%ss.list(0, 2, ""))
    """.format(model.capitalize,model.capitalize)

    val result = new StringBuffer()

    result.append(genModelHome)

    result.append(genList)

    result.append(genSave)

    result.append(genDelete)

    result.append(genEdit)

    result.append(genUpdate)

    result.append(genCreate)

    result.toString
  }

}
