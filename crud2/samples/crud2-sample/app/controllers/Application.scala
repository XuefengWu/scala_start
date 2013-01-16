package controllers

import language.experimental.macros
import play.api._
import play.api.mvc._
import crud2.controllers.Hello

object Application extends Controller {

  case class User(name: String, age: String)

  def index = Action {
    val user:User = Hello.get[User]
    Ok(views.html.index(Hello.hello()+user+Hello.add(1)(2)))
  }
  

  
}