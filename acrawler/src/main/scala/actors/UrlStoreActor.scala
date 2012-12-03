package actors

import akka.actor.{ Props, Actor }
import akka.routing.RoundRobinRouter
import scala.collection.mutable.ListBuffer
import scala.collection.convert.WrapAsJava._

import com.mongodb._

class UrlStoreActor extends Actor {

  //val loadRouter = context.actorOf(Props[UrlLoadActor].withRouter(RoundRobinRouter(Conf.nrOfUrlLoader)), name = "urlLoadActorRouter")
  val loadRouter = context.actorOf(Props[UrlLoadActor])	
	
  val db = new MongoClient().getDB("crawler")
  val linksColl = db.getCollection("linksCollection")
  val loadedLinksColl = db.getCollection("loadedLinksColl")

  def receive = {
    case Urls(links: Seq[String]) => {
      val results = Set[String](links: _*)
      //save to DB
      val unsaved = results.filterNot(lnk => {
        val cursor = linksColl.find(new BasicDBObject("url", lnk))
        try {
          cursor.hasNext()
        } finally {
          cursor.close()
        }
      })
      unsaved.foreach(lnk => {
        linksColl.insert(new BasicDBObject("url", lnk))
      })
      //start new url
      println("unsaved:" + unsaved.size)
      val unloaded = results.filterNot(lnk => {
        val cursor = loadedLinksColl.find(new BasicDBObject("url", lnk))
        try {
          cursor.hasNext()
        } finally {
          cursor.close()
        }
      })
      println("unloaded:" + unloaded.size)
      unloaded.foreach(loadRouter ! Url(_))
 
    }
    case Loaded(url: String) => {
      loadedLinksColl.insert(new BasicDBObject("url", url))
    }
  }

}