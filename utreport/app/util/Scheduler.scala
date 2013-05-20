package util

import java.util.Calendar
import play.libs.Akka
import scala.concurrent.ExecutionContext
import play.api.Logger
import akka.actor.Cancellable

object Scheduler {
  import scala.concurrent.duration.Duration
  import scala.concurrent.duration.DAYS
  import scala.concurrent.duration.MINUTES
  import scala.concurrent.duration.SECONDS

  val tasks = scala.collection.mutable.ListBuffer[Cancellable]()
  
  def registerDailyJob(job: () => Unit, hour: Int, minute: Int, sec: Int) {
    implicit val ec = ExecutionContext.Implicits.global

    val initialDelayS = getNextInitialDelayMs( hour, minute,sec) / 1000;

    Logger.debug("registerDailyJob " + initialDelayS+"s")
    val task = Akka.system.scheduler.schedule(Duration.create(initialDelayS, SECONDS), Duration.create(1, DAYS))(job())
    tasks += task
  }

  def clear() {
    tasks.foreach(_.cancel())
  }
  def registerDailyJob(job: () => Unit, hour: Int, minute: Int) {
      registerDailyJob(job,hour,minute,0)
  }

  def getNextInitialDelayMs( hour: Int, minute: Int,sec:Int): Long = {
    val from = Calendar.getInstance()
    var next: Calendar = from.clone().asInstanceOf[Calendar]
    next.set(Calendar.HOUR_OF_DAY, hour)
    next.set(Calendar.MINUTE, minute)
    next.set(Calendar.SECOND, sec)
    if (next.compareTo(from) < 0)
      next.add(Calendar.DATE, 1)

    next.getTimeInMillis - from.getTimeInMillis
  }

}

 