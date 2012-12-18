package actors

import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory
import akka.routing.RoundRobinRouter

object Main extends App {

  val system = ActorSystem("CrawlerSystem")

  println(system.dispatcher)
  println(system.dispatchers.defaultDispatcherConfig.getInt("mailbox-capacity"))
  println(system.dispatchers.defaultGlobalDispatcher)
  val domain = "tutorialspoint.com"

  val  storActor = system.actorOf(Props[UrlStoreActor])//.withRouter(RoundRobinRouter(Conf.nrOfUrlLoader)))
  storActor ! Result(List("http://tutorialspoint.com"))
  storActor ! Restart
}