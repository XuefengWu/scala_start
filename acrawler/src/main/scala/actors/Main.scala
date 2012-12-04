package actors

import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory

object Main extends App {

  val system = ActorSystem("CrawlerSystem")

  println(system.dispatcher)
  println(system.dispatchers.defaultDispatcherConfig.getInt("mailbox-capacity"))
  println(system.dispatchers.defaultGlobalDispatcher)
  val domain = "tutorialspoint.com"

  system.actorOf(Props[UrlLoadActor]) ! Url("http://tutorialspoint.com")

}