package actors

import scala.io.Source
import akka.actor.{Props,Actor}
import java.io.File
import org.apache.commons.io.FileUtils

class DataActor extends Actor {

  val parserActor = context.actorOf(Props[PageParseActor])
  
  def receive = {
    case Data(url:String,bytes: Array[Byte]) => {
      val urlFile = new File(util.UrlFile.buildFilePath(url))
      if(!urlFile.exists()){
    	  FileUtils.writeByteArrayToFile(urlFile, bytes)
      }
      
      if(isHtml(url)) {
    	  parserActor ! Contents(url,Source.fromBytes(bytes).getLines().mkString("\n"))
      }
      
    }
  }
  
  private def isHtml(url:String) = !(url.endsWith("gif") || url.endsWith("png") || url.endsWith("jpg"))
 
}