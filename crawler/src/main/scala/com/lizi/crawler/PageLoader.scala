package com.lizi.crawler

// Bonus Problem: Make the size follow the links on a given page,
// and load them as well. For example, a sizer for google.com
// would compute the size for Google and all of the pages it
// links to.

import scala.io._
import java.io.File

// I am singleton object that contains access methods for URLs.
object PageLoader {

  // I get the content at the given URL.
  def getContent(murl: String): String = {

    import dispatch._

    val file = new File(getFilePath(murl))
    if (file.exists && file.isFile) {
      return Source.fromFile(getFilePath(murl)).mkString
    }

    val svc = url(murl)
    val content = try {
      if (murl.endsWith("png") || murl.endsWith("jpg") || murl.endsWith("gif")) {
        val res = Http(svc OK as.Bytes).apply
        save(murl, res)
        ""
      } else {
        val res = Http(svc OK as.String).apply
        save(murl, res)
        res
      }

    } catch {
      case _ => ""
    }

    content
  }

  def getFilePath(url: String) = {

    val _url = url.replace("http://", "")

    val base = "/site"

    val pageNameIndex = _url.lastIndexOf("/")

    val dir = if (pageNameIndex > 1) {
      base + "/" + _url.dropRight(_url.length - pageNameIndex)
    } else {
      base + "/" + _url
    }

    val pageName = if (pageNameIndex > 1) {
      _url.drop(pageNameIndex + 1)
    } else {
      "siehome.html"
    }

    if (!new File(dir).exists) {
      new File(dir).mkdirs()
    }

    (dir + "/" + pageName).replaceAll(":", "")
  }

  def save(url: String, bytes: Array[Byte]) {
    import org.apache.commons.io.FileUtils

    val filePath = getFilePath(url)

    FileUtils.writeByteArrayToFile(new File(filePath), bytes)
  }
  
  def save(url: String, content: String) {

    if (content.length < 3) return

    import org.apache.commons.io.FileUtils

    val filePath = getFilePath(url)

    FileUtils.writeStringToFile(new File(filePath), content)
  }

}

object PageLoaderTest extends App {
  PageLoader.save("http://www.tutorialspoint.com/ant/index.htm", "hello")
}
 