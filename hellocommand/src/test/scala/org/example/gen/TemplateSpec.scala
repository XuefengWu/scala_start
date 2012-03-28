package org.example.gen

import org.specs2.mutable._
import java.io.File

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-28
 * Time: 下午3:44
 * To change this template use File | Settings | File Templates.
 */

class TemplateSpec  extends SpecificationWithJUnit  {
  val fields = List(("name","String"),("lastUpdated","DateTime"))
  val model = "todo"
  "hello StringTemplate " should {
    import org.stringtemplate.v4._
    "work with string" in {

      val hello = new ST("Hello,<name>")
      hello.add("name","world")
      hello.render must contain ("world")
    }
    "work with file " in {
      val lines = scala.io.Source.fromFile("src/main/resource/list.html").mkString
      println(lines)
      val st = new ST(lines,'$','$')
      st.add("model", model)
      val result = st.render()
      println(result)
      result must contain (model)
    }
  }
  "gen Template " should {
    "start with  name" in {
      val res = Template.genList(model,fields)
      println(res)
      res must contain(model)
    }

  }
}