package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def upload = Action(parse.multipartFormData) {
    request =>
      request.body.file("picture").map {
        picture =>
          import java.io.File
          val filename = picture.filename
          val contentType = picture.contentType

          val dir: File = new File("/tmp/picture")
          dir.mkdirs()
          val dest = new File(dir.getAbsolutePath+File.separator+filename)
          picture.ref.moveTo(dest)
          Ok(s"$filename uploaded")
      }.getOrElse {
        Redirect(routes.Application.index).flashing(
          "error" -> "Missing file"
        )
      }
  }


}