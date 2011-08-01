package controllers

import play._
import play.mvc._
import models.User
 

object Application extends Controller {

  import views.Application._

  def index = { 
	val users = User.users
    html.index("Your Scala application is ready!",users)
  }

}
