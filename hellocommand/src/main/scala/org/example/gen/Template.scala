package org.example.gen

import java.io.{File, FileWriter}
import org.stringtemplate.v4.ST

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */

object Template {


  def genList(model:String,fields:List[(String,String)])={
    val heads = fields.map(f => "<th>%s</th>".format(f._1.capitalize)).mkString("\n\t\t")
    val rows = fields.map{ f =>
      val rowST = new ST("<td><a href=\"@routes.$MM$s.edit(v.id.get)\">@v.$f$</a></td>",'$','$')
      rowST.add("MM",model.capitalize)
      rowST.add("f",f._1)
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

  def genEdit(model:String,fields:List[(String,String)]) ={
    val inputFields = fields.map{ f =>
      val inputFieldST = new ST("@inputText(<m>Form(\"<f>\"), '_label -> \"<m> <f>\")")
      inputFieldST.add("m",model)
      inputFieldST.add("f",f._1)
      inputFieldST.render()
    }.mkString("\n\t")

    val ins = getClass.getResourceAsStream("/template/edit.html")
    val lines = scala.io.Source.fromInputStream(ins).mkString
    val st = new ST(lines,'$','$')
    st.add("m", model)
    st.add("MM", model.capitalize)
    st.add("inputFields", inputFields)
    st.render()

  }
  def genCreate(model:String,fields:List[(String,String)])= {
    val inputFields = fields.map{ f =>
      val inputFieldST = new ST("@inputText(<m>Form(\"<f>\"), '_label -> \"<m> <f>\")")
      inputFieldST.add("m",model)
      inputFieldST.add("f",f._1)
      inputFieldST.render()
    }.mkString("\n\t")

    val ins = getClass.getResourceAsStream("/template/create.html")
    val lines = scala.io.Source.fromInputStream(ins).mkString
    val st = new ST(lines,'$','$')
    st.add("m", model)
    st.add("MM", model.capitalize)
    st.add("inputFields", inputFields)
    st.render()
  }

}
