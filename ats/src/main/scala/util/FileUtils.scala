package util

import scala.xml._
import scala.collection.mutable.Stack
import java.io.FileOutputStream
import java.io.File
import java.util.UUID
import java.nio.channels.Channels
import conf.Conf
import java.net.URL
import java.net.HttpURLConnection
import java.text.SimpleDateFormat

object FileUtils {

  def createTempFile(fileName: String, content: String) = {
    val tmpFile = Conf.tempPath + fileName
    val Encoding = "UTF-8"
    val pp = new PrettyPrinter(80, 2)
    val fos = new FileOutputStream(tmpFile)
    val writer = Channels.newWriter(fos.getChannel(), Encoding)

    try {
      writer.write(content)
    } finally {
      writer.close()
    }
    tmpFile
  }

  def copyDirToTemp(dir: String): String = {
    val src = new File(Conf.filePath + dir)
    val dest = Conf.tempPath + dir.drop(1).dropRight(src.getName.length())
    org.apache.commons.io.FileUtils.copyDirectoryToDirectory(src, new File(dest))
    dest + src.getName
  }

  def copyToTemp(src: String, rename: Boolean = false): String = {
    val srcFile = if (src.startsWith("/")) {
      Conf.filePath + src
    } else {
      src
    }
    copyFileToTemp(new File(srcFile), rename)
  }
  private def copyFileToTemp(srcFile: File, rename: Boolean = false): String = {
    val ext = srcFile.getName().drop(srcFile.getName().lastIndexOf("."))
    val tmp = if (rename) {
      Conf.tempPath + UUID.randomUUID().toString() + ext
    } else {
      Conf.tempPath + srcFile.getName()
    }
    if(!tmp.equals(srcFile.getAbsolutePath())){
      copy(srcFile, new File(tmp))  
    }
    
    tmp
  }
  def copy(src: String, dest: String) {
    copy(new File(src), new File(dest))
  }

  def copy(src: File, dest: File) {
    if(!dest.exists()){
      println("copy from %s to %s" format (src.getAbsolutePath(), dest.getAbsolutePath()))
    org.apache.commons.io.FileUtils.copyFile(src, dest)  
    }
     
  }

  def copyPatientInstanceToTodayImage(pid: String) {
    patientInstanceFiles(pid).foreach(f => copy(f, new File(Conf.filePath + "image/" + f.getName())))
  }
  private def copyPatientInstanceToTemp(pid: String): Seq[File] = {
    val tmpName = UUID.randomUUID().toString()
    val tmpPath = Conf.tempPath + tmpName
    if (new File(tmpPath).mkdir()) {
      patientInstanceFiles(pid).foreach(f => copy(f, new File(tmpPath + "/" + f.getName())))
      val tmp = new File(tmpPath)
      tmp.listFiles()
    } else {
      Nil
    }
  }

  def deleteFile(path: String): Boolean = deleteFile(new File(path))
  def deleteFile(file: File): Boolean = {
    val deleted = file match {
      case null => false
      case f if (!f.exists()) => false
      case f if (f.isFile()) => f.delete()
      case f if (f.isDirectory()) => {
        f.listFiles().foreach(deleteFile)
        f.delete()
      }
    }
    println("delete file: " + file.getAbsolutePath() + ": " + deleted)
    deleted
  }

  def save(node: Node, fileName: String) = {

    val Encoding = "UTF-8"
    val pp = new PrettyPrinter(80, 2)
    val fos = new FileOutputStream(fileName)
    val writer = Channels.newWriter(fos.getChannel(), Encoding)

    try {
      writer.write("<?xml version='1.0' encoding='" + Encoding + "'?>\n")
      writer.write(XMLUtils.OutEscape(pp.format(node)))
    } finally {
      writer.close()
    }

    fileName
  }

  def checkExists(pid: String, instanceId: String, postLength: Int = 4) = {
    println("check intance %s in %s" format (instanceId, pid))
    val inss = patientInstanceFiles(pid).map(_.getName().dropRight(postLength))

    inss.contains(instanceId)
  }

  def exists(fileUrl: URL) = {
    try {
      HttpURLConnection.setFollowRedirects(false)
      // note : you may also need
      //        HttpURLConnection.setInstanceFollowRedirects(false)
      val con = fileUrl.openConnection().asInstanceOf[HttpURLConnection]
      con.setRequestMethod("HEAD")
      con.getResponseCode() == HttpURLConnection.HTTP_OK
    } catch {
      case e: Exception => {
        e.printStackTrace()
        false
      }
    }
  }

  def patientInstanceFiles(pid: String) = {
    val patientPath = Conf.patientBasePath + pid.takeRight(2) + "/" + pid
    val insFiles = new File(patientPath).listFiles().flatMap(_.listFiles())
    insFiles
  }

  private def cleanTodayFiles() {
    deleteFile(Conf.filePath + today)
  }

  private val month = {
    val dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM")
    dateformatYYYYMMDD.format(new java.util.Date())
  }
  private val today = {
    val dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd")
    dateformatYYYYMMDD.format(new java.util.Date())
  }
  val todayLogPath: String = {
    val logFile = new File("log")
    if (!logFile.exists()) {
      logFile.mkdir()
    }
    val tlp = "log/" + today + "/"
    val tlf = new File(tlp)
    if (!tlf.exists()) {
      tlf.mkdir()
    }
    tlp
  }

  def getFileAbsPath(dir: String, name: String) = Conf.filePath + dir + "/" + name

  val rawImages = scala.collection.mutable.Stack[String]()
  def rawImageFile() = {
    rawFile(rawImages, "image", "image")
  }

  val raw3ds = scala.collection.mutable.Stack[String]()
  def raw3dFile() = {
    rawFile(raw3ds, "3d", "3d")
  }

  private def rawFile(raws: Stack[String], src: String, tmp: String) = {
    var result = ""
    synchronized {
      if (raws.isEmpty) {
        val dir = new File(Conf.filePath + src)
        val tmpDir = new File(Conf.tempPath + tmp)
        if (!tmpDir.exists() || tmpDir.list().length == 0) {
          org.apache.commons.io.FileUtils.copyDirectory(dir, tmpDir)
        }
        raws.pushAll(tmpDir.listFiles().map(_.getAbsolutePath()))
      }
      result = raws.pop
    }
    result
  }

}