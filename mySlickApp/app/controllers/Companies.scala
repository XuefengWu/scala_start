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

import models.Company
import models.Page

object Companies extends Controller  {

  implicit lazy val database = Database.forDataSource(DB.getDataSource())

  val companyForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "name" -> nonEmptyText)(Company.apply)(Company.unapply))

  def create = Action {
    Ok(html.company.create(companyForm))
  }

  def save = Action {
    implicit request =>
      companyForm.bindFromRequest.value map {
        company =>
          database withSession {
            (models.Companies insert company)
          }
          Redirect(routes.Companies.list())
      } getOrElse BadRequest
  }

  def view(id: Long) = Action {
    val company:Option[Company] = models.Companies.get(id)
    
    Ok(html.company.view(company))
  }
  
  def list(page: Int, orderBy: Int, filter: String) = Action {
    val companies = models.Companies.list(page, orderBy = orderBy, filter = filter)
    Ok(html.company.list(companies,page,filter))
  }

  def edit(id: Long) = Action {
    val company:Option[Company] = models.Companies.get(id)
    company.map{ v =>
       Ok(html.company.edit(id,companyForm.fill(v)))
    }.getOrElse(NotFound)
    
  }

  def update(id: Long) = Action {
    implicit request =>
      companyForm.bindFromRequest.value map {
        company =>
          models.Companies.update(id, company)
          Redirect(routes.Companies.view(id))
      } getOrElse BadRequest
  }

  def delete(id: Long) = Action {
    implicit request =>
      companyForm.bindFromRequest.value map {
        company =>
          database withSession {
            models.Companies.delete(id)
          }
          Redirect(routes.Companies.list())
      } getOrElse BadRequest
  }

}