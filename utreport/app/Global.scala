import play.api.GlobalSettings
import models._
import play.api.db.DB
import play.api.Application
import play.api.Play.current
import scala.slick.driver.H2Driver.simple._
import Database.threadLocalSession
import util.Scheduler
import controllers.TestCaseService

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Scheduler.registerDailyJob(TestCaseService.importAndSend _, 12,33)
  }

  override def onStop(app: Application) {
    Scheduler.clear()
  }
}