package actors

import akka.actor.{ Props, Actor }
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.InputStream

import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager
import org.apache.commons.httpclient.methods.GetMethod

class UrlLoadActor extends Actor {

  val dataActor = context.actorOf(Props[DataActor])
  val logActor = context.actorOf(Props[LogActor])
  val storeActor = context.actorOf(Props[UrlStoreActor])
  val client = new HttpClient(new MultiThreadedHttpConnectionManager())

  def load(url: String): InputStream = {
    val startOn = System.currentTimeMillis()
    val get = new GetMethod(url)
    val iGetResultCode = client.executeMethod(get)
    get.setFollowRedirects(true)
    val res = get.getResponseBodyAsStream()
    println("spend %ss to load: \t%s, \t%s".format((System.currentTimeMillis() - startOn) / 1000, url, this))
    res
  }
  def receive = {
    case Url(url: String) => {
      logActor ! LogStart(this)
      val urlFile = new File(util.UrlFile.buildFilePath(url))
      println(this + ": " + url + ": " + urlFile.exists())
      if (urlFile.exists()) {
        storeActor ! Loaded(url)
        storeActor ! Restart
      } else {
        storeActor ! Loaded(url)
        if (url.contains(Main.domain) && !url.contains("=http://")) {
          dataActor ! Data(url, load(url))
        } else {
          storeActor ! Restart
        }
      }

    }
  }

}