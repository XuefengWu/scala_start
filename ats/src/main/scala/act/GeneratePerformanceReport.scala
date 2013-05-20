package act

import scala.io.Source
import org.apache.commons.io.FileUtils
import java.io.File
import scala.io.Codec

object GeneratePerformanceReport extends App {

  implicit val codec = Codec("UTF-8")

  def cleanLogForPerformanceAnalysis(logPath: String) {
    //clean data

    def isTimeRecord(text: String) = {
      //val timePattern = """\d\d \d\d \d\d\d\d \d\d""".r
      val timePattern = """\d\d\d\d-\d\d-\d\d \d\d""".r
      (timePattern findFirstIn text).nonEmpty
    }

    def convertToTime(hour: String, min: String, second: String, millionSecond: String): Int =
      hour.toInt * 60 * 60 * 1000 + min.toInt * 60 * 1000 + second.toInt * 1000 + millionSecond.toInt

    def extractTimeUsage(text: String): (Int, String) = {
      //val usagePattern = """(\d\d):(\d\d):(\d\d):(\d\d\d).*([com|org]\..*)""".r
      val usagePattern = """(\d\d):(\d\d):(\d\d)\.(\d\d\d).*([com|org]\..*)""".r
      usagePattern findFirstIn text match {
        case Some(usagePattern(h, m, s, ms, module)) => (convertToTime(h, m, s, ms), module)
        case None => (0, text)
      }
    }

    def removeUseless(lines: Iterator[(Int, String)]) = {
      val start = lines.next._1
      lines.map(p => (p._1 - start, p._2))
    }

    println(Source.fromFile(logPath).getLines.filter(isTimeRecord))

    val lines = Source.fromFile(logPath).getLines.filter(isTimeRecord).map(extractTimeUsage)

    val data = removeUseless(lines).map(p => p._1 + "\t" + p._2).mkString("\n").replaceAll("com.carestreamhealth.", "").replaceAll("com.carestream.", "")

    println(data)

    FileUtils.writeStringToFile(new File(logPath + ".log"), data)
  }

  val logDir = "D:/tmp/logforanalysis"
  new File(logDir).listFiles().foreach(f => cleanLogForPerformanceAnalysis(f.getAbsolutePath()))

}