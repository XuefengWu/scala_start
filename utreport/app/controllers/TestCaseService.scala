package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import dal._
import play.api.db.DB
import play.api.Play.current
import scala.reflect.runtime.universe._
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

import scala.slick.jdbc.{ GetResult, StaticQuery => Q }
import Q.interpolation

object TestCaseService {

  implicit val getTestCaseDiffResult = GetResult(r => TestCaseDiff(r.<<, r.<<))

  lazy val database = Database.forDataSource(DB.getDataSource())

  def getLastTestCaseDiff(): Seq[TestCaseDiff] = {
    database withSession {
      val trs = TestReport.findAll.sortBy(_.id.desc).take(2).list

      if (trs.size == 2) {
        val rId1 = trs.head.id
        val rId2 = trs.tail.head.id

        val res = sql"SELECT  CONCAT(t.classname,'.', t.name)  ts , count(id)  tc FROM  testcase t  where (t.reportId = $rId1 or t.reportId = $rId2)  group by  ts HAVING tc < 2 ".as[TestCaseDiff].list
        res
      } else {
        Nil
      }
    }
  }

  def importAndSend() {
    Logger.debug("TestCaseService.importAndSend")

    //ignore Friday and Saturday
    val day = new Date(System.currentTimeMillis()).getDay()
    if (day == 5 || day == 6) {
      return
    }

    importUTResult()
    Logger.debug("start sendReport ")

    sendReport()
  }

  def sendReport(idOpt: Option[Long] = None) = {
    database withSession {
      Logger.debug("start sendReport ")
      val id = idOpt.getOrElse {
        TestReport.findAll.filter(_.date === new Date(System.currentTimeMillis())).sortBy(_.id.desc).take(1).list.head.id.get
      }
      Logger.debug("sendReport: " + id)
      val tcs = TestCases.findAll(id).filter(f => f.typ === "failure" || f.typ === "error").list
      val diff = getLastTestCaseDiff

      val smtpHost = play.api.Play.current.configuration.getString("smtp.host").getOrElse(throw new RuntimeException("smtp.host needs to be set in application.conf in order to use this plugin (or set smtp.mock to true)"))
      val props = new Properties()
      props.setProperty("mail.transport.protocol", "smtp")
      props.setProperty("mail.host", smtpHost)
      val mailSession = javax.mail.Session.getDefaultInstance(props, null)

      val message = new MimeMessage(mailSession)

      val subject = new java.lang.StringBuffer()
      subject.append("UT Report: " + new Date(System.currentTimeMillis()));
      subject.append("[")
      if (tcs.size > 0 || diff.size > 0) {
        
        if (tcs.size > 0) {
          subject.append("ooOps, ")
          subject.append(tcs.size)
          subject.append(" tests failed. ")
        }
        if (tcs.size > 0) {
          subject.append(diff.size)
          subject.append(" tests updated")
        }
        
      } else {
        subject.append("Enn.., everything looks good, nothing happened.")
      }
      subject.append("]")

      message.setSubject(subject.toString());

      val from = play.api.Play.current.configuration.getString("ut.mail.from").getOrElse(throw new RuntimeException("ut.mail.from needs to be set in application.conf in order to use this plugin"))
      message.setFrom(new InternetAddress(from))

      val tos = play.api.Play.current.configuration.getString("ut.mail.to").getOrElse(throw new RuntimeException("ut.mail.to needs to be set in application.conf in order to use this plugin"))
      val toAdds = tos.split(",").map(new InternetAddress(_))
      message.setRecipients(Message.RecipientType.TO, tos)
      //message.addRecipient(Message.RecipientType.TO,toAdds)

      val multipart = new MimeMultipart("related")
      val messageBodyPart = new MimeBodyPart()

      val site = play.api.Play.current.configuration.getString("ut.site").getOrElse(throw new RuntimeException("ut.site needs to be set in application.conf in order to use this plugin"))
      val report = html.testcases.mail(tcs, site, getLastTestCaseDiff)
      messageBodyPart.setContent(report.toString, "text/html")
      multipart.addBodyPart(messageBodyPart)

      //image part
      val messageBodyPart2 = new MimeBodyPart()
      val fds2 = new FileDataSource("public/images/reports/testcases.png")
      messageBodyPart2.setDataHandler(new DataHandler(fds2))
      messageBodyPart2.setHeader("Content-ID", "<testcases>")
      multipart.addBodyPart(messageBodyPart2)

      val messageBodyPart3 = new MimeBodyPart()
      val fds3 = new FileDataSource("public/images/reports/test_fails.png")
      messageBodyPart3.setDataHandler(new DataHandler(fds3))
      messageBodyPart3.setHeader("Content-ID", "<test_fails>")
      multipart.addBodyPart(messageBodyPart3)

      // put everything together
      message.setContent(multipart)

      val transport = mailSession.getTransport()
      transport.connect()
      transport.sendMessage(message,
        message.getRecipients(Message.RecipientType.TO))
      transport.close()

    }
  }
  def importUTResult() = {
    database withSession {

      val srcDir = play.api.Play.current.configuration.getString("ut.root").getOrElse(throw new RuntimeException("ut.root needs to be set in application.conf in order to use this plugin"))
      val projects = new File(srcDir).listFiles()

      def shotClassName(classname: String) = {
        if (classname.contains("csdm")) {
          classname.substring(classname.indexOf("csdm") + "csdm".length() + 1)
        } else if (classname.contains("pas")) {
          classname.substring(classname.indexOf("pas") + "pas".length() + 1)
        } else {
          classname
        }
      }
      val tests: Seq[TestCase] = projects.filter(_.isDirectory()).map(p => {

        val res = util.GenerateUtErrorReport.scanProject(p.getAbsolutePath(), "target/surefire-reports").map(tc => {

          val name = (tc \ "@name").toString
          val classname = (tc \ "@classname").toString
          val time = (tc \ "@time").toString.toDouble
          val msg = tc.toString

          val cases: Seq[Option[TestCase]] = if (tc.child.isEmpty) {
            List(Some(TestCase(None, 0L, name, shotClassName(classname), time, "", "success")))
          } else {
            tc.child.map(node =>
              node match {
                case <failure>{ _ }</failure> => Some(TestCase(None, 0L, name, shotClassName(classname), time, msg, "failure"))
                case <error>{ _ }</error> => Some(TestCase(None, 0L, name, shotClassName(classname), time, msg, "error"))
                case _ => None
              })
          }
          cases.filter(_.isDefined).map(_.get)
        })
        res.flatten
      }).flatten.toSeq

      val reportOpt = TestReport.findAll.filter(_.date === new Date(System.currentTimeMillis())).take(1).list.headOption
      if (tests.size > 100) {
        val failureSize = tests.filter(p => p.typ == "failure").size
        val errorSize = tests.filter(p => p.typ == "error").size

        if (reportOpt.isDefined) {
          val reportId = reportOpt.get.id.get
          TestReport.find(reportId).map(v => v.total ~ v.fails ~ v.errors).update((tests.size, failureSize, errorSize))

          //delete preview test cases
          TestCases.delete(reportId)
        }
        val reportId = reportOpt.map(_.id.get).getOrElse {
          val q = TestReport.insert(TestReport(None, new Date(System.currentTimeMillis()), tests.size, failureSize, errorSize))
          val scopeIdentity = SimpleFunction.nullary[Long]("LAST_INSERT_ID")
          Query(scopeIdentity).list.head
        }

        tests.foreach(v => TestCases.insert(v.copy(reportId = reportId)))

        val dir = new java.io.File("public/images/reports")
        dir.mkdirs()

        //render test report
        val reports = TestReport.findAll().sortBy(_.id.desc).take(12).list.reverse
        val data_total = reports.map(v => ((v.total.toDouble, getDateAxisX(v))))
        val dataset_total = LineChartUtil.createCategoryDataset(data_total, "Test Cases")
        val chart_total = LineChartUtil.createLineChart(dataset_total, "Total Unit Test")
        chart_total.getPlot().asInstanceOf[CategoryPlot].getRangeAxis().asInstanceOf[NumberAxis].setRange(200, 900)
        chart_total.saveAsPNG(new java.io.File("public/images/reports/testcases.png"), (800, 268))

        val data_fails = reports.map(v => ((v.errors + v.fails.toDouble, getDateAxisX(v))))
        val dataset_fails = LineChartUtil.createCategoryDataset(data_fails, "Failed Test Cases")
        val chart_fails = LineChartUtil.createLineChart(dataset_fails, "Failed Unit Test")
        chart_fails.saveAsPNG(new java.io.File("public/images/reports/test_fails.png"), (800, 268))

        //UT time
        val data_utduration = reports.map(v => ((getTimeSpend(v.id.get), getDateAxisX(v))))
        val dataset_utduration = LineChartUtil.createCategoryDataset(data_utduration, "Test Cases Spend")
        val chart_utduration = LineChartUtil.createLineChart(dataset_utduration, "Total Unit Test Spend")
        chart_utduration.saveAsPNG(new java.io.File("public/images/reports/test_utduration.png"), (800, 268))
      }
    }
  }

  private def getTimeSpend(reportId: Long): Double = {
    Query(TestCases.findAll(reportId).map(_.time).sum).list.head.get
  }
  private def getDateAxisX(t: models.TestReport): String = {
    (t.date.getMonth() + 1) + "." + t.date.getDate()
  }

}