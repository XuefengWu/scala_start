package actors

import akka.actor.{ Props, Actor }
import akka.routing.RoundRobinRouter
import scala.collection.mutable.ListBuffer
import scala.collection.convert.WrapAsJava._
import scala.util.control.Breaks._
import scala.collection.mutable.HashSet

object UrlStoreActor  {
  val all = scala.collection.mutable.HashSet[String]().par
  val loaded = scala.collection.mutable.HashSet[String]().par
}
class UrlStoreActor extends Actor {

  import UrlStoreActor._
  
  val loadRouter = context.actorOf(Props[UrlLoadActor])
  val logActor = context.actorOf(Props[LogActor])

  def receive = {
    case Urls(links: Seq[String]) => {
      logActor ! LogStart(this)

      all ++= links

      val unloaded = links.filterNot(p => loaded.contains(p))

      println("all:%s, unloaded:%s".format(all.size, unloaded.size))

      unloaded.foreach(loadRouter ! Url(_))
      if (unloaded.isEmpty) {
        self ! Restart
      }
    }
    case Loaded(url: String) => {
      logActor ! LogStart(this)

      loaded += url

    }
    case Restart => {
      import scala.util.control.Breaks._
      logActor ! LogStart(this)
      breakable {
        all.foreach { f =>
          if (loaded.contains(f)) {
            loadRouter ! Url(f)
            self ! Loaded(f)
            break
          }
        }
      }
    }
  }

}