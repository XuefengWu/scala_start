package util

import actors.Conf
import java.io.File

object UrlFile {

  def buildFilePath(url: String) = {

    val _url = if (url.endsWith("/")) {
      url.replace("http://", "").dropRight(1) + ".html"
    } else {
      url.replace("http://", "")
    }

    val base = Conf.baseDir

    val pageNameIndex = _url.lastIndexOf("/")

    val dir = if (pageNameIndex > 1) {
      base + "/" + _url.dropRight(_url.length - pageNameIndex)
    } else {
      base + "/" + _url
    }

    val pageName = if (pageNameIndex > 1 && url.lastIndexOf(".") > url.length - 6) {
      _url.drop(pageNameIndex + 1).replaceAll("\\?", "")
    } else {
      "page.html"
    }

    if (!new File(dir).exists) {
      new File(dir).mkdirs()
    }

    (dir + "/") + pageName.replaceAll(":", "")
  }

}