package org.example.gen

import java.io.{File, FileWriter}
import org.stringtemplate.v4.ST
import org.example.ScaffoldPlugin

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */

object Template {


  def genList(model:String,fields:Seq[(String, String)])={
    import ScaffoldPlugin.modelField
    val heads = fields.map(f => "<th>%s</th>".format(f._1.capitalize)).mkString("\n\t\t")
    val rows = fields.map{ f =>
      val rowST = new ST("<td><a href=\"@routes.$MM$s.edit(v.id.get)\">@v.$f$</a></td>",'$','$')
      rowST.add("MM",model.capitalize)
      rowST.add("f",modelField(f))
      rowST.render()
    }.mkString("\n\t\t")

    val ins = getClass.getResourceAsStream("/template/list.html")
    val lines = scala.io.Source.fromInputStream(ins).mkString
    val st = new ST(lines,'$','$')
    st.add("m", model)
    st.add("MM", model.capitalize)
    st.add("heads", heads)
    st.add("rows", rows)
    st.render()
  }

  private def makeFormField(model:String,fields:Seq[(String, String)]) = {
    fields.map{ f =>
      val inputHtml:String = ScaffoldPlugin.extraPureType(f._2) match {
        case t if ScaffoldPlugin.isModelType(t) => {
          val selectInputST = new ST(
          """
           @select(
                <m>Form("<f>"), 
                <fm>.options, 
                '_label -> "<fm>", '_default -> "-- Choose a <fm> --",
                '_showConstraints -> false
            )
          """)
          selectInputST.add("m",model)
          selectInputST.add("f",f._1)
          selectInputST.add("fm",t)
          selectInputST.render()
        }
        case "Boolean" => "@checkbox(<m>Form(\"<f>\"), '_label -> \"<m> <f>\")"
        case "Date" => "@inputDate(<m>Form(\"<f>\"), '_label -> \"<m> <f>\")"
        case _ => "@inputText(<m>Form(\"<f>\"), '_label -> \"<m> <f>\")"
      }
      val inputFieldST = new ST(inputHtml)
      inputFieldST.add("m",model)
      inputFieldST.add("f",f._1)
      inputFieldST.render()
    }.mkString("\n\t")
  }
  def genEdit(model:String,fields:Seq[(String, String)]) ={
    val inputFields = makeFormField(model,fields)

    val ins = getClass.getResourceAsStream("/template/edit.html")
    val lines = scala.io.Source.fromInputStream(ins).mkString
    val st = new ST(lines,'$','$')
    st.add("m", model)
    st.add("MM", model.capitalize)
    st.add("inputFields", inputFields)
    st.render()

  }
  def genCreate(model:String,fields:Seq[(String, String)])= {
    val inputFields = makeFormField(model,fields)

    val ins = getClass.getResourceAsStream("/template/create.html")
    val lines = scala.io.Source.fromInputStream(ins).mkString
    val st = new ST(lines,'$','$')
    st.add("m", model)
    st.add("MM", model.capitalize)
    st.add("inputFields", inputFields)
    st.render()
  }

}
