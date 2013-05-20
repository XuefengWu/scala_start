package util

import scala.xml._
import scala.collection.mutable.Stack
import java.io.FileOutputStream
import java.io.File
import java.util.UUID
import java.nio.channels.Channels
import scala.xml.Elem


object FileUtils {

  def save(node: Node, fileName: String) = {

    val Encoding = "UTF-8"
    val pp = new PrettyPrinter(80, 2)
    val fos = new FileOutputStream(fileName)
    val writer = Channels.newWriter(fos.getChannel(), Encoding)

    try {
      writer.write("<?xml version='1.0' encoding='" + Encoding + "'?>\n")
      writer.write(outEscape(pp.format(node)))
    } finally {
      writer.close()
    }

    fileName
  }


  
  /** Escapes a raw string for use in XML.*/

def escape(s: String) =
  {
    val out = new StringBuilder
    for (i <- 0 until s.length) {
      s.charAt(i) match {
        case '>' => out.append("&gt;")
        case '&' => out.append("&amp;")
        case '<' => out.append("&lt;")
        case '"' => out.append("&quot;")
        case c => out.append(c)
      }
    }
    out.toString
  }

def outEscape(s: String) =
  {
    s.replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&quot;", "\"").replaceAll("&#xd;", "")
  }
  
def outEscape(e: Elem): String = {
  outEscape(e.toString())
}
   



}

  