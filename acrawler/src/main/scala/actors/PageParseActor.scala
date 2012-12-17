package actors

import akka.actor.{ Props, Actor, ActorRef }
import util.PageParse

class PageParseActor(listener: ActorRef) extends Actor {

  val logActor = context.actorOf(Props[LogActor])

  def receive = {
    case Contents(url: String, contents: String) => {
      logActor ! LogStart(this)
      val urls = PageParse.getLinks(url, contents)
      listener ! Result(urls)
    }
  }

}