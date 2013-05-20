package act

import scala.io.Source
import scala.collection.mutable.ListBuffer
import java.io.File
import scala.io.Codec
import org.apache.commons.io.FileUtils
object IssueScanner extends App {

  implicit val codec = Codec("latin1")

  def scanMatch(lines: Iterator[String], start: String, end: String) = {
    var startTag = false
    var endTag = false
    var currentInputLine = ("", 0)
    val result = ListBuffer[(String, Int)]()
    lines.zipWithIndex.foreach { line =>
      if (line._1.contains(start)) {
        if (startTag) {
          result += ((currentInputLine._1, currentInputLine._2 + 1))
        }
        startTag = true
        currentInputLine = line
      }
      if (line._1.contains(end)) {
        if (!startTag) {
          result += ((currentInputLine._1, currentInputLine._2 + 1))
        }
        startTag = false
      }
    }
    result.toIterator
  }
  def scanAcqDmn(file: File): Iterator[(String, Int)] = {
    println("scan: " + file.getName())
    val lines = Source.fromFile(file).getLines
    val res = scanMatch(lines, "[ Acquisition Input ]", "[ Acquisition Output ]")
    res
  }

  def scanDataMgr(file: File): Iterator[(String, Int)] = {
    println("scan: " + file.getName())
    val lines = Source.fromFile(file).getLines
    val res = scanMatch(lines, "CS Data Manager Controller Started", "CS Data Manager Controller Stopped")
    res
  }

  def scanPAS(file: File): Iterator[(String, Int)] = {
    println("scan: " + file.getName())
    val lines = Source.fromFile(file).getLines
    val res = lines.zipWithIndex.filter(l => l._1.contains("ERROR") || l._1.contains("WARN"))
      .filterNot(_._1.contains("CommandSender,create socket errorjava.net.ConnectException"))
      .filterNot(_._1.contains("CommandSender,needRegister for host[localhost] port[10000]"))
      .filterNot(_._1.contains("sdo.matcher.PropMapper"))
      .filterNot(_._1.contains("failed:taskkill  /F /IM Reconstruction.exe"))
      .filterNot(_._1.contains("failed to send message appName="))
      .filterNot(_._1.contains("needRegister for host[localhost] port[2012]"))
      .filterNot(_._1.contains("AppState,no thread wait 2DViewer"))
      .filterNot(_._1.contains("An existing connection was forcibly closed by the remote host"))
      .filterNot(_._1.contains("AxisFault: Connection refused: connect"))
      .filterNot(_._1.contains("Dozer configuration file not found: dozer.properties.  Using defaults for all Dozer global properties"))
      .filterNot(_._1.contains("TempFileManager,failed to clean temporary files"))
      .filterNot(_._1.contains("An instance for HTTP will be configured automatically. Please update your axis2.xml file!"))
      .filterNot(_._1.contains("image with same SOP Instance UID already exists"))
      .filterNot(_._1.contains("image with same SOP Instance UID already created and belongs to another patient"))
      .map(l => (l._1, l._2 + 1))
    res
  }

  val CSAcqDmnPattern = """CSAcqDmn.*\.csv.*""".r
  val CSDataMgrPattern = """CSDataMgr.*\.csv.*""".r
  val PASPattern = """csdm-.*-log.*\.csv""".r

  def scan(f: File): (File, Iterator[(String, Int)]) =
    f.getName() match {
      case CSAcqDmnPattern() => (f, scanAcqDmn(f))
      case CSDataMgrPattern() => (f, scanAcqDmn(f))
      case PASPattern() => (f, scanPAS(f))
      case _ => (f, Nil.toIterator)
    }

  def scanDir(file: File) {
    if (file.isDirectory()) {
      file.listFiles().map(scanDir(_))
    } else {
      val res = scan(file)
      if (!res._2.isEmpty) {
        val dir = file.getParent().replace("tmp", "tmp/report")
        if (!new File(dir).exists()) {
          new File(dir).mkdirs()
        }
        val path = dir + "/_" + file.getName() + ".error"
        FileUtils.writeStringToFile(new File(path), res._2.map(l => l._2 + "\t" + l._1) mkString ("\n"))
      }
    }
  }

  val base = """D:\tmp\EK_HI00167803 acq temp files\WangLiu\log"""
  scanDir(new File(base))
}