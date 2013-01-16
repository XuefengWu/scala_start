package controllers

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.libs.json._

import play.api.db._
import play.api.Play.current

import views._

// Use H2Driver to connect to an H2 database
// Use H2Driver to connect to an H2 database

// Use the implicit threadLocalSession
//import Database.threadLocalSession

import scala.slick.session.Session

import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

import models.Computer
import models.Page

/**
 * Created with IntelliJ IDEA.
 * User: Orna
 * Date: 13-1-14
 * Time: 下午9:29
 * To change this template use File | Settings | File Templates.
 */
object Computers extends Controller{

  implicit lazy val database = Database.forDataSource(DB.getDataSource())

  val computerForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText,
    "introduced" -> optional(sqlDate),
      "discontinued" -> optional(sqlDate),
      "companyId" -> optional(longNumber)
      )(Computer.apply)(Computer.unapply))

  def create = Action {
    Ok(html.computer.create(computerForm))
  }

  def save = Action {
    implicit request =>
      computerForm.bindFromRequest.value map {
        computer =>
          database withSession {
            (models.Computers insert computer)
          }
          Redirect(routes.Computers.list())
      } getOrElse BadRequest
  }

  def view(id: Long) = Action {
    val computer:Option[Computer] = models.Computers.get(id)

    Ok(html.computer.view(computer))
  }

  def list(page: Int, orderBy: Int, filter: String) = Action {
    val computers = models.Computers.list(page, orderBy = orderBy, filter = filter)
    Ok(html.computer.list(computers,page,filter))
  }

  def edit(id: Long) = Action {
    val computer:Option[Computer] = models.Computers.get(id)
    computer.map{ v =>
      Ok(html.computer.edit(id,computerForm.fill(v)))
    }.getOrElse(NotFound)

  }

  def update(id: Long) = Action {
    implicit request =>
      computerForm.bindFromRequest.value map {
        computer =>
          models.Computers.update(id, computer)
          Redirect(routes.Computers.view(id))
      } getOrElse BadRequest
  }

  def delete(id: Long) = Action {
    implicit request =>
      computerForm.bindFromRequest.value map {
        computer =>
          database withSession {
            models.Computers.delete(id)
          }
          Redirect(routes.Computers.list())
      } getOrElse BadRequest
  }

}
