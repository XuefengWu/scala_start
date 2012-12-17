package actors

import akka.actor.{ Props, Actor }
import akka.routing.RoundRobinRouter
import com.mongodb._
import scala.collection.convert.WrapAsJava._

object UrlStoreActor {
  val started = new scala.collection.mutable.HashSet[String].par

}
class UrlStoreActor extends Actor {

  val urlloadActor = context.actorOf(Props[UrlLoadActor])//.withRouter(RoundRobinRouter(Conf.nrOfUrlLoader)))
  //val urlloadActor = context.actorOf(Props(new UrlLoadActor(self)))
  val logActor = context.actorOf(Props[LogActor]) //.withRouter(RoundRobinRouter(Conf.nrOfUrlLoader)), name = "urlLoadActorRouter")

  val db = new MongoClient("localhost", new MongoClientOptions.Builder().cursorFinalizerEnabled(false).build()).getDB("crawler")
  val linksColl = db.getCollection("linksCollection")
  val loadedLinksColl = db.getCollection("loadedLinksColl")

  def receive = {
    case Result(links: Seq[String]) => {
      logActor ! LogStart(this)
      println(Thread.currentThread().getName()+"--"+this + ":" + links.size)
      //save to DB
      val unsaved = links.filterNot(p => UrlStoreActor.started.contains(p)) 
      linksColl.insert(unsaved.map(new BasicDBObject("url", _)).toSeq)
 
      //start new url
      val unloaded = links.filterNot(p => UrlStoreActor.started.contains(p)) 

      unloaded.foreach { url =>
        urlloadActor ! Url(url)
        UrlStoreActor.started += url
      }

    }
    case Loaded(url: String) => {
      loadedLinksColl.insert(new BasicDBObject("url", url))
    }
    case Restart => {
      val loaded = new java.util.HashSet[String]
      val allLinks = new java.util.HashSet[String]
      import scala.util.control.Breaks._
      println(this + ":  Restart \t sender:" + sender.path)
      logActor ! LogStart(this)
      val loadedCursor = loadedLinksColl.find()
      try {
        while (loadedCursor.hasNext()) {
          loaded.add(loadedCursor.next().get("url").toString())
        }
      } finally {
        loadedCursor.close()
      }
      println("all loaded: %s ".format(loaded.size))
      val cursor = linksColl.find()
      try {
        while (cursor.hasNext()) {

          try {
            allLinks.add(cursor.next().get("url").toString())
          }
        }
      } finally {
        cursor.close()
      }
      println("all allLinks: %s ".format(allLinks.size))

      val it = allLinks.iterator()
      while (it.hasNext()) {
        val url = it.next()
        if (!loaded.contains(url) && !UrlStoreActor.started.contains(url)) {
          UrlStoreActor.started += url
          urlloadActor ! Url(url)
          //println("UrlStoreActor.allLinks:"+UrlStoreActor.allLinks.size + " UrlStoreActor.loaded:"+UrlStoreActor.loaded.size + " UrlStoreActor.started:" +UrlStoreActor.started.size)
          //break
        }
      }
    }

  }

}