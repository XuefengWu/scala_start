package act

import scala.xml._

object ConvertXmlToJavadoc {

  var listBuff = new scala.collection.mutable.ListBuffer[(String,String)]
  var labelBuff = new scala.collection.mutable.HashSet[String]
  
  def updateText(parent:Node,node: Node) {
    node match {
      case Elem(prefix, label, attribs, scope, child @ _*) => {
        labelBuff += label
        child.foreach(updateText(node,_))
      }
      case Text(v) if !v.trim().isEmpty() => {
       listBuff += ((v,"%%%s%%".format(parent.label))) 
      }
    }
  }
  //load xml from file
  val listObjElem = scala.xml.XML.loadFile("D:/Test/listobject.xml")

  // find text
  updateText(listObjElem,listObjElem)
  //listObjElem.foreach(updateText(_))
  var listObjDoc = listObjElem.toString
  labelBuff.foreach(l => listObjDoc = listObjDoc.replace(l,"tns:"+l))
  listBuff.foreach(vl => listObjDoc = listObjDoc.replaceFirst(vl._1, vl._2))
  println(listObjDoc)
  // replace text with %tag_name%

}