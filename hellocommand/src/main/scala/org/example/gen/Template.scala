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
    import ScaffoldPlugin.{modelField,caseClassWithReference,fieldsWithModel,isModelType,extraPureType,hasReferenceModel}

    val heads = fields.zipWithIndex.map{case (f,i) => "@header(%s,\"%s\")".format(i+1,f._1.capitalize)}.mkString("\n\t\t")
    val rows = fields.map{ f =>
      val rowST = new ST("<td><a href=\"@routes.$MM$s.edit($mid$)\">@$m$.$f$</a></td>",'$','$')
      if(isModelType(f._2)){
        rowST.add("MM",extraPureType(f._2))
        if(f._2.contains("Required")){
          rowST.add("f","toString")
        } else {
          rowST.add("f","getOrElse(\"--\").toString")
        }

        rowST.add("m",f._1)
        if(f._2.contains("Required")){
          rowST.add("mid",f._1+".id.get")
        }else{
          rowST.add("mid",f._1+" match {case Some(v) => v.id.getOrElse(0);case None => 0}")
        }

      } else {
        rowST.add("MM",model.capitalize)

        if(f._2.contains("Required")){
          rowST.add("f",modelField(f))
        } else {
          rowST.add("f",modelField(f)+".getOrElse(\"--\").toString")
        }
        rowST.add("mid",model+".id.get")
        rowST.add("m",model)
      }

      rowST.render()
    }.mkString("\n\t\t")

    val caseFields = {
      if (hasReferenceModel(fields)) {
        fieldsWithModel(model,fields).map(_._1).mkString("  case (",",",")")
      }else{
        model
      }
    }
    val ins = getClass.getResourceAsStream("/template/list.html")
    val lines = scala.io.Source.fromInputStream(ins).mkString
    val st = new ST(lines,'$','$')
    st.add("m", model)
    st.add("MM", model.capitalize)
    st.add("heads", heads)
    st.add("rows", rows)
    st.add("caseClassWithReference",caseClassWithReference(model,fields))
    st.add("caseFields",caseFields)
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
