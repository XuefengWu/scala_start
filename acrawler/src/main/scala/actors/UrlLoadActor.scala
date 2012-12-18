package actors

import java.io.File
import java.io.InputStream
import com.ning.http.client.AsyncHttpClient
import Data.apply
import Loaded.apply
import LogStart.apply
import akka.actor.{ Actor, ActorRef, Props }
import akka.actor.actorRef2Scala
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
import org.apache.commons.io.FileUtils
import util.PageParseUtil
import scala.io.Source

class UrlLoadActor   extends Actor {
  val logActor = context.actorOf(Props[LogActor])

  //val asyncHttpClient = new AsyncHttpClient()

  /*
  def loadAsync(url: String): InputStream = {
    val f = asyncHttpClient.prepareGet(url).execute()
    f.get().getResponseBodyAsStream()
  }
  */

  def load(url: String): Option[Array[Byte]] = {
    val client = new HttpClient()
    val get = new GetMethod(url)
    val iGetResultCode = client.executeMethod(get)
    if (iGetResultCode == 200) {
      val is = get.getResponseBody()
      get.releaseConnection()
      Some(is)
    } else {
      get.releaseConnection()
      None
    }
  }

  def receive = {
    case Url(url: String) => {
      logActor ! LogStart(this)
      val urlFile = new File(util.UrlFile.buildFilePath(url))
      if (urlFile.exists()) {
        sender ! Loaded(url)
      } else {
        if (url.contains(Main.domain) && !url.contains("=http://")) {
          load(url).foreach(bytes => {
            if(!urlFile.getParentFile().exists()){
	          urlFile.getParentFile().mkdirs()
	        }
            FileUtils.writeByteArrayToFile(urlFile, bytes)
            sender ! Result(PageParseUtil.getLinks(url, Source.fromBytes(bytes).getLines().mkString("\n")))
          })
        }
        sender ! Loaded(url)
      }

    }
    case e => println(e)

  }

}