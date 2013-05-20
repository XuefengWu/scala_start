package act

import scala.io.Source
import org.apache.commons.io.FileUtils
import java.io.File
import scala.io.Codec
object LogLexical extends App {

  val logPattern = """^.*(\d\d:\d\d:\d\d):\d{3}.*,\[(.+)\],(\w+) ?,.*([com|org]\..*)""".r

  implicit val codec = Codec("UTF-8")

  val dataPath = "D:/tmp/csdm-server-log.12-09-17.0.csv"

  def extractKeyWords(line: String): String = {
    logPattern findFirstIn line match {
      case Some(logPattern(time, thread, loglevel, logmsg)) => "%s\t%s\t%s\t%s".format(time, thread, loglevel, logmsg)
      case None => ("")
    }
  }

  val result = Source.fromFile(dataPath).getLines.map(l => extractKeyWords(l)).filterNot(_.isEmpty()).toList

  val data = result.mkString("\n")

  println(data)

  FileUtils.writeStringToFile(new File(dataPath + ".log"), data)

}