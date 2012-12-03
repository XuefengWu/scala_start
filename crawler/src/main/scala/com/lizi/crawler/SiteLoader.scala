package com.lizi.crawler

import scala.collection.parallel.mutable.ParHashSet
import akka.actor._
import akka.routing.RoundRobinRouter
import akka.util.Duration
import akka.util.duration._
import scala.collection.mutable.ListBuffer

object SiteLoader extends App {

  println("---------------")
  val base = "http://www.tutorialspoint.com"
  load(10, base)
  println("---------------")
  println("SiteLoader Finished:")

  sealed trait LoadMessage
  case class Task(works: Seq[String]) extends LoadMessage
  case class Work(url: String) extends LoadMessage
  case class Result(urls: Seq[String]) extends LoadMessage

  val worked = ParHashSet[String]()

  class Worker extends Actor {

    def getLinks(url: String) = {
 
        LinkPareser.getLinks(PageLoader.getContent(url))
          .map(ln => LinkPareser.getAbstracLinkInSite(ln, url, base))
          .filter(_.startsWith(base))
          .filterNot(_.contains("/cgi-bin/"))
          .filterNot(_.contains("/forums/"))
    }

    def receive = {
      case Work(url) =>
        worked += url
        print(".")
        if(worked.size % 200 == 0){
          println(worked.size)
        }
        sender ! Result(getLinks(url)) // perform the work
    }
  }

  class Master(nrOfWorkers: Int) extends Actor {

    var nrOfResults: Int = 0
    var totalWorks: Int = 0
    val start: Long = System.currentTimeMillis
    val results = ParHashSet[String]()

    val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(nrOfWorkers)), name = "workerRouter")

    def receive = {
      case Task(works) => {
        totalWorks = works.size
        if (totalWorks == 0) {
          println("------------------no works, finished!---------")
          context.stop(self)
          context.system.shutdown()
          println("------------------no works, should shutdown!---------")
        }
        works.foreach(workerRouter ! Work(_))
      }

      case Result(urls) => {
        nrOfResults += 1

        urls.foreach(results += _)

        if (totalWorks == nrOfResults) {
          val newWorks = ListBuffer[String]()
          newWorks ++= results.filterNot(worked.contains(_)).toIterator
          self ! Task(newWorks)
          
          println("\n------------ finished %d works, start new %d works---------\n".format(totalWorks,newWorks.size))
          results.clear
        }

      }
    }

  }

  def load(nrOfWorkers: Int, startUrl: String) {
    // Create an Akka system
    val system = ActorSystem("crawlerSystem")

    // create the master
    val master = system.actorOf(Props(new Master(nrOfWorkers)), name = "master")

    // start the calculation
    master ! Task(List(startUrl))

  }

}

 