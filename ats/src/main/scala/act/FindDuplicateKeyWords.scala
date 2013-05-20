package act

import scala.io.Source
import org.apache.commons.io.FileUtils
import java.io.File
import scala.io.Codec

trait KeyWordPatterns {

  val PatientFullNamePattern = """^%(\w{8}-\w{4}-\w{4}-\w{4}-\w{12})%.*;%(.*)%;;%(.*)%;;;%other%;;;;;"""
}
object FindDuplicateKeyWords extends App with KeyWordPatterns {

  implicit val codec = Codec("UTF-8")

  val dataPath = "D:/tmp/drpitts/CSDMDB_2.0.3.0_20121205@110000/patient.dat"

  def extractKeyWords(line: String): (String, String, String) = {
    val keyWordPattern = PatientFullNamePattern.r
    keyWordPattern findFirstIn line match {
      case Some(keyWordPattern(uid, first, last)) => (line, first + " " + last, uid)
      case None => (line, "", "")
    }
  }

  val result = Source.fromFile(dataPath).getLines.map(l => extractKeyWords(l)).toList

  val fullnames = result.map(l => l._2).toList

  val duplicate = result.filter(l => fullnames.count(n => n == l._2) > 1)

  val data = duplicate.map(l => l._3 + "\t" + l._2 + "\t" + l._1).mkString("\n")

  result.foreach(l => {
    val name = l._2
    println(name)
    println(fullnames.count(n => n == name))
    println(name)
  })

  FileUtils.writeStringToFile(new File(dataPath + ".log"), data)

}