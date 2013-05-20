package service


import scala.xml.{ Elem, XML }
import util.XMLUtils._

object SoapClient {
  private def error(msg: String) = {
    println("SoapClient error: " + msg)
  }

  def wrap(xml: Elem): String = {
    val buf = new StringBuilder
    buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n")
    buf.append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n")
    buf.append("<SOAP-ENV:Body>\n")
    buf.append(xml.toString)
    buf.append("\n</SOAP-ENV:Body>\n")
    buf.append("</SOAP-ENV:Envelope>\n")
    buf.toString
  }
 def axis2Wrap(pkg:String,xml: String) = {
      """
		<soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" %s>
		   <soap:Header/>
		   <soap:Body>
		      %s
		   </soap:Body>
		</soap:Envelope>
          """ format (pkg,xml)
    }
 
  private def sendMessage(host: String, action:String,req: String): Option[String] = {
    val url = new java.net.URL(host)
    val outs = req.getBytes
    val conn = url.openConnection.asInstanceOf[java.net.HttpURLConnection]
    
    def withdrawContent(raw:String):String = {
      try {
	 val TRYOPHY = """(?s).*(<trophy type="result" version="1.0">.*</trophy>).*""".r
         val TRYOPHY(v) = raw
         v
      } catch {
        case me: MatchError => raw
        case e:Exception => throw e
      }
    }
    try {
      conn.setRequestMethod("POST")
      conn.setDoOutput(true)
      conn.setRequestProperty("Content-Length", outs.length.toString)
      conn.setRequestProperty("Content-Type", "application/soap+xml;charset=UTF-8;action=\"urn:%s\"" format(action))
      conn.getOutputStream.write(outs)
      conn.getOutputStream.close
      val response = conn.getInputStream
      val content = scala.io.Source.fromInputStream(response).getLines().mkString("\n")
      val result = OutEscape(content)
      val v = withdrawContent(result)
      Some(OutEscape(v))
    } catch {
      case e: Exception =>
        error("post: " + e)
        error("post:" + scala.io.Source.fromInputStream(conn.getErrorStream).mkString)
        None
    }
  } 
  def invoke(host: String,pck:String, action:String, req: String)(implicit fw:java.io.FileWriter): Option[String] = {
    val startAt = System.currentTimeMillis();
    val input = axis2Wrap(pck,req)
    val runnerName = java.lang.Thread.currentThread().getName() +"-" + java.lang.Thread.currentThread().getId()
    val in = "\n%s Input: -%s-\n%s "format(action,runnerName,input) 
    println(in)
    fw.write(in)
    
    val result = sendMessage(host, action, input)
    
    val finishAt = System.currentTimeMillis();
    val out = "\n%s Output: -%s- using:%dms\n%s "format(action,runnerName,(finishAt-startAt),result.getOrElse("no result")) 
    println(out)
    fw.write(out)
    result
    
  }
  
 
}