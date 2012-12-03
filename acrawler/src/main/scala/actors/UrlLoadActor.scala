package actors

import akka.actor.{Props,Actor}
import org.apache.commons.io.FileUtils
import java.io.File

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
import org.apache.commons.httpclient.methods.GetMethod

class UrlLoadActor extends Actor {

  val dataActor = context.actorOf(Props[DataActor])
  
  val client = new HttpClient(new MultiThreadedHttpConnectionManager())
  
  def load(url:String):Array[Byte] = {
   val urlFile = new File(util.UrlFile.buildFilePath(url))
   if(urlFile.exists()){
       FileUtils.readFileToByteArray(urlFile)
   } else {
	   val startOn = System.currentTimeMillis()
	   val  get = new GetMethod(url)
	   val  iGetResultCode = client.executeMethod(get)
	   get.setFollowRedirects(true)
	   val res = get.getResponseBody()
	   println("spend %ss to load: \t%s".format((System.currentTimeMillis() - startOn)/1000,url))
	   res
   }
  }
  def receive = {
    case Url(url:String) => if(url.contains(Main.domain) && !url.contains("=http://")) {
      dataActor ! Data(url,load(url))
      sender ! Loaded(url)
    }
  }

}