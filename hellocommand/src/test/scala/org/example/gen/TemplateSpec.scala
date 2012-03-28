package org.example.gen

import org.specs2.mutable._
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
      val g = new STGroupFile("list.stg")
      val st:ST = g.getInstanceOf("decl")
      st.add("type", "int")
      st.add("name", "x")
      st.add("value", 0)
      val result = st.render()
      println(result)
      result must contain ("int")
    }
  }
  "gen Template " should {
    "start with  name" in {
      val res = Template.genList(model,fields)
      println(res)
      res must contain("name")
    }

  }
}