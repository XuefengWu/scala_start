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

import play.api.db._
import play.api.Play.current

// Use H2Driver to connect to an H2 database

import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession

import Database.threadLocalSession


object Application extends Controller {

  lazy val database = Database.forDataSource(DB.getDataSource())

 

  def index = Action {
    Redirect(routes.Companies.list())
  }
 


}