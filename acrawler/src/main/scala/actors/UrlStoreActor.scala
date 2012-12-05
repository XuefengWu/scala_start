package actors

import akka.actor.{ Props, Actor }
import akka.routing.RoundRobinRouter
import scala.collection.mutable.ListBuffer
import scala.collection.convert.WrapAsJava._
import scala.util.control.Breaks._
import com.mongodb._

object UrlStoreActor {
  val started = scala.collection.mutable.HashSet[String]().par
}
class UrlStoreActor extends Actor {

  val loadRouter = context.actorOf(Props(new UrlLoadActor(self)).withRouter(RoundRobinRouter(Conf.nrOfUrlLoader)))
  //val loadRouter = context.actorOf(Props(new UrlLoadActor(self)))
  val logActor = context.actorOf(Props[LogActor].withRouter(RoundRobinRouter(Conf.nrOfUrlLoader)), name = "urlLoadActorRouter")

  val db = new MongoClient("localhost", new MongoClientOptions.Builder().cursorFinalizerEnabled(false).build()).getDB("crawler")
  val linksColl = db.getCollection("linksCollection")
  val loadedLinksColl = db.getCollection("loadedLinksColl")

  def receive = {
    case Result(links: Seq[String]) => {
      logActor ! LogStart(this)
      println(this + ":" + links.size)
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
      val unloaded = results.filterNot(lnk => {
        val cursor = loadedLinksColl.find(new BasicDBObject("url", lnk))
        try {
          cursor.hasNext()
        } finally {
          cursor.close()
        }
      })
      println("unsaved:%s, unloaded:%s".format(unsaved.size, unloaded.size))
      unloaded.foreach(loadRouter ! Url(_))

      if (unloaded.isEmpty) {
        self ! Restart
      }
    }
    case Loaded(url: String) => {
      logActor ! LogStart(this)
      loadedLinksColl.insert(new BasicDBObject("url", url))
    }
    case Restart => {
      import scala.util.control.Breaks._
      println(this + ":" + Restart + "\t sender:" + sender.path)
      logActor ! LogStart(this)
      val cursor = linksColl.find()
      try {
        breakable {
          while (cursor.hasNext()) {
            val url = cursor.next().get("url").toString()
            val loadedCursor = loadedLinksColl.find(new BasicDBObject("url", url))
            val loaded = loadedLinksColl.count()
            try {
              if (!loadedCursor.hasNext() && !UrlStoreActor.started.contains(url)) {
                println("saved links size:%s, loaded size: %s, started: %s ".format(cursor.size(), loaded, UrlStoreActor.started.size))
                UrlStoreActor.started += url
                loadRouter ! Url(url)
                //break
              }
            } finally {
              loadedCursor.close()
            }
          }
        }
      } finally {
        cursor.close()
      }
    }
  }

}