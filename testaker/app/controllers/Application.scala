package controllers

import play.api.mvc.Action._
import play.api.Routes
import controllers.routes.javascript._
import play.api.mvc.{Action, Controller}

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-4-10
 * Time: 下午4:00
 * To change this template use File | Settings | File Templates.
 */

object Application extends Controller{

  // -- Javascript routing

  def javascriptRoutes = Action {
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Questions.index,Questions.list,Questions.save,Questions.delete,
        Questions.show,Questions.edit,Questions.update,Questions.create
      )
    ).as("text/javascript")
  }
}
