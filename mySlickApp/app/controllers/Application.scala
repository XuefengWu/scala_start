package controllers

import play.api._
import play.api.mvc._


import play.api.data.Form

import play.api.data._
import play.api.data.Forms._

// Use H2Driver to connect to an H2 database

import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
//import Database.threadLocalSession

import scala.slick.session.Session
import play.api.libs.json._

import models.{Bar, Bars}
import play.api.db._
import play.api.Play.current

// Use H2Driver to connect to an H2 database

import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession

import Database.threadLocalSession


object Application extends Controller {

  lazy val database = Database.forDataSource(DB.getDataSource())


  val barForm = Form(
    mapping(
      "name" -> text
    )
      ((name) => Bar(None, name))

      ((bar: Bar) => Some(bar.name))

  )

  def index = Action {
    Ok(views.html.index(barForm))
  }

  def addBar = Action {
    implicit request =>
      barForm.bindFromRequest.value map {
        bar =>
          database withSession {
            (Bars insert bar)
          }
          Redirect(routes.Application.index())
      } getOrElse BadRequest
  }

  def getBars = Action {
    val json = database withSession {
      val bars = for (b <- Bars) yield b.name.toString
      Json.toJson(bars.list)
    }
    Ok(json).as(JSON)
  }


}