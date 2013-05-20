package util

import scala.xml._

object XMLUtils {

  def getInstanceIdFrom(res: String, insType: String):String = {
    val node = XML.loadString(res) \ (insType)
    val id = XMLUtils.getParameterValue(node, "internal_id")
    id
  }
  def getParameterValue(node: NodeSeq, key: String):String = getParameterValue(node,("key",key))
  

 def getParameterValue(node: NodeSeq, keyValues: (String,String)*):String = {
    (node \ "parameter").filter(elem => {
      val ks = keyValues.map{ f =>
          elem \ ("@"+f._1) text
      }
      
      keyValues.map(_._2).diff(ks).isEmpty
       
    }).head \ ("@value") text
  }
    
  /** Escapes a raw string for use in XML.*/
  object Escape {
    def apply(s: String) =
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
  }

  object OutEscape {
    import scala.xml.Elem
    def apply(s: String) =
      {
        s.replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&quot;", "\"").replaceAll("&#xd;", "")
      }
    def apply(e: Elem): String = {
      apply(e.toString())
    }
  }

}