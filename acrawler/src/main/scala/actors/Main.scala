package actors

import akka.actor.{ActorSystem,Props}

object Main extends App {

  val system = ActorSystem()
  val domain = "tutorialspoint.com"
  system.actorOf(Props[UrlLoadActor]) ! Url("http://tutorialspoint.com")

}