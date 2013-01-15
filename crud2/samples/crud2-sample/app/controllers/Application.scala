package controllers

import language.experimental.macros
import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    
    Ok(views.html.index(hello()))
  }
  
  def hello() = macro crud2.macros.Hello.hello_impl
  
}