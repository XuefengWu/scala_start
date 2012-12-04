package actors

import scala.io.Source
import akka.actor.{ Props, Actor }
import java.io.File
import java.io.InputStream
import org.apache.commons.io.FileUtils
import java.io.FileOutputStream
import akka.routing.RoundRobinRouter

class DataActor extends Actor {

  val parserActor = context.actorOf(Props[PageParseActor])
  val logActor = context.actorOf(Props[LogActor])

  def receive = {
    case Data(url: String, is: InputStream) => {
      logActor ! LogStart(this)
      val urlFile = new File(util.UrlFile.buildFilePath(url))
      if (!urlFile.exists()) {
        val os = new FileOutputStream(urlFile)
        org.apache.commons.io.IOUtils.copy(is, os)
        os.close()
      }
      if (isHtml(url)) {
        parserActor ! Contents(url, Source.fromInputStream(is).getLines().mkString("\n"))
      } else {
        println(url + " is not a html")
      }
      is.close()
    }
  }

  private def isHtml(url: String) = !(url.endsWith("gif") || url.endsWith("png") || url.endsWith("jpg"))

}