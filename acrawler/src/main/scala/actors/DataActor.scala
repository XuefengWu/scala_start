package actors

import scala.io.Source
import akka.actor.{Props,Actor}
import java.io.File
import org.apache.commons.io.FileUtils

class DataActor extends Actor {

  val parserActor = context.actorOf(Props[PageParseActor])
  
  def receive = {
    case Data(url:String,bytes: Array[Byte]) => {
      FileUtils.writeByteArrayToFile(new File(util.UrlFile.buildFilePath(url)), bytes)
      parserActor ! Contents(url,Source.fromBytes(bytes).getLines().mkString("\n"))
    }
  }
 
}