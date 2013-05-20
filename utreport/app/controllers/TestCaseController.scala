package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import dal._
import play.api.db.DB
import play.api.Play.current
import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession
import models._
import scala.xml._
import java.io.File
import java.sql.Date
import scalax.chart._
import scalax.chart.Charting._
import util.LineChartUtil
import org.jfree.chart.axis.NumberAxis
import play.api.Play.current
import java.util.Properties
import javax.mail.internet._
import javax.activation._
import javax.mail.Message

class TestCaseController(testCaseComponent: TestCaseComponent = new TestCaseComponentImpl) extends Controller {

  lazy val database = Database.forDataSource(DB.getDataSource())

  val pageSize = 50

  val Home = Redirect(routes.TestCaseController.index)

  def index = Action { implicit request =>
    database withSession {
      val list = TestReport.findAll.sortBy(_.date.desc).take(100).list
      Ok(html.testcases.list(Page(list, 0, 100, list.size), 0,TestCaseService.getLastTestCaseDiff))
    }
  }
  
  def view(id:Long) = Action { implicit request =>
    database withSession {
      val tc = TestCases.find(id).list.head
      Ok(html.testcases.view(tc))
    }
  } 

  /**
   * Display the paginated list.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on entity names
   */
  def list(reportId: Long, page: Int, orderBy: Int) = Action { implicit request =>
    database withSession {
      val reportOpt = TestReport.find(reportId).take(1).list.headOption
      
      Ok(html.testcases.cases(reportId,
        Page(testCaseComponent.list(reportId, page, pageSize, orderBy).list,
          page,
          offset = pageSize * page,
          reportOpt.map(_.total.toLong).getOrElse(0L)),
        orderBy))
    }
  }

  def send(reportId: Long) = Action { implicit request =>

    TestCaseService.sendReport(Some(reportId))

    Home.flashing("success" -> "send Finished!")

  }

  def mail(reportId: Long) = Action { implicit request =>
    database withSession {
      val site = play.api.Play.current.configuration.getString("ut.site").getOrElse(throw new RuntimeException("ut.site needs to be set in application.conf in order to use this plugin"))
      Ok(html.testcases.mail(testCaseComponent.list(reportId, 0, 100, 2).filter(f => f.typ === "failure" || f.typ === "error").list,site,TestCaseService.getLastTestCaseDiff))
    }
  }

  def imports() = Action { implicit request =>
    TestCaseService.importUTResult()
    Home.flashing("success" -> s"Import Finished!")
  }

}

object TestCaseController extends TestCaseController(new TestCaseComponentImpl)