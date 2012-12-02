package actors

import akka.actor.{Props,Actor}
import com.ning.http.client.{AsyncHttpClient,Response}
import org.apache.commons.io.FileUtils
import java.io.File

class UrlLoadActor extends Actor {

  val dataActor = context.actorOf(Props[DataActor])
  
  val asyncHttpClient = new AsyncHttpClient()
  def load(url:String):Array[Byte] = {
    println("start load:\t"+url)
   val urlFile = new File(util.UrlFile.buildFilePath(url))
   if(urlFile.exists()){
       FileUtils.readFileToByteArray(urlFile)
   } else {
	   val f = asyncHttpClient.prepareGet(url).execute()
	   f.get().getResponseBodyAsBytes()
   }
  }
  def receive = {
    case Url(url:String) => if(url.contains(Main.domain)) {
      dataActor ! Data(url,load(url))
      sender ! Loaded(url)
    }
  }

}