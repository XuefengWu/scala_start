package act

import java.io.File
import scala.xml._
import util.DataPrepare
import util.FileUtils
import conf.Conf

object GenerateReports {
  def main(args: Array[String]) {

    //clean test data
    DataPrepare.tryDeleteAllPatient()(new java.io.FileWriter("log/" + this.getClass().getName() + ".xml"))
    cleanPatientDir()
    //cleanTemp() 

    val pp = new PrettyPrinter(80, 2)

    val testReportPath = "target\\test-reports"
    val reportFileName = "report.xml"
    val reportFilePath = FileUtils.todayLogPath + "\\" + reportFileName
    val reportDir = new File("testReportPath")
    if (!reportDir.exists()) {
      reportDir.mkdirs()
    }

    "".split("")
    val testReportDir = new File(testReportPath)
    val testResultFiles: Seq[File] = if (testReportDir != null && testReportDir.exists()) {
      testReportDir.listFiles().filter(_.getName().takeRight(3).equalsIgnoreCase("xml"))
        .filterNot(_.getName().equalsIgnoreCase(reportFileName))
    } else {
      Nil
    }

    var report = <report></report>
    var failures = <failures></failures>
    var success = <success></success>
    var errors = <errors></errors>

    testResultFiles.foreach(f => {
      val testcase = XML.loadFile(f) \ ("testcase")
      testcase.foreach(tc => tc.child.foreach(node => {
        node match {
          case <failure>{ _ }</failure> => failures = addChild(failures, tc)
          case <error>{ _ }</error> => errors = addChild(errors, tc)
          case _ => success = addChild(success, tc)
        }
      }))
    })

    println("----success----")
    success.child.foreach(s => {
      val tc = (s \ ("@classname")).text
      println(tc.drop(tc.lastIndexOf(".") + 1))
    })

    println("----failures----")
    failures.child.foreach(s => {
      val tc = (s \ ("@classname")).text
      println(tc.drop(tc.lastIndexOf(".") + 1))
    })

    println("----errors----")
    errors.child.foreach(s => {
      val tc = (s \ ("@classname")).text
      println(tc.drop(tc.lastIndexOf(".") + 1))
    })

    println("totoal success: " + success.child.size)
    println("totoal failures: " + failures.child.size)
    println("totoal errors: " + errors.child.size)

    val total = <total><failures>{ failures.child.size }</failures><errors>{ errors.child.size }</errors><success>{ success.child.size }</success></total>

    report = addChild(report, failures)
    report = addChild(report, errors)
    report = addChild(report, total)

    FileUtils.save(report, reportFilePath)

  }

  def addChild(n: Node, newChild: Node) = n match {
    case Elem(prefix, label, attribs, scope, child @ _*) =>
      Elem(prefix, label, attribs, scope, child ++ newChild: _*)
    case _ => error("Can only add children to elements!")
  }

  def cleanTemp(): Boolean = {
    val tmp = new File(Conf.tempPath)
    val filesDeleted = tmp.listFiles().map(FileUtils.deleteFile)
    if (filesDeleted.isEmpty) {
      true
    } else {
      filesDeleted.reduceLeft(_ && _)
    }
  }

  def cleanPatientDir() {
    val patientPath = new File(Conf.patientBasePath)
    org.apache.commons.io.FileUtils.deleteDirectory(patientPath)    
  }

}