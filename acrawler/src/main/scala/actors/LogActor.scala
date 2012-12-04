package actors

import scala.io.Source
import akka.actor.{ Props, Actor }
import java.io.File
import java.io.InputStream
import org.apache.commons.io.FileUtils
import java.io.FileOutputStream
import akka.routing.RoundRobinRouter

class LogActor extends Actor {

  def receive = {
    case LogStart(actor: Actor) => {
      //println(actor)
    }
  }

}