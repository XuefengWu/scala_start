package org.example.gen

import org.specs2.mutable._
import java.io.File
import org.example.ScaffoldPlugin

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

  }
  "gen list Template " should {
    "with model" in {
      val res = Template.genList(model,fields)
      println(res)
      fields.foreach(f => res must contain (f._1))
      res must contain(model)
    }
  }

    "gen create Template " should {
      "with model" in {
        val res = Template.genCreate(model,fields)
        println(res)
        fields.foreach(f => res must contain (f._1))
        res must contain(model)
      }

  }

  "gen edit Template " should {
    "with model" in {
      val res = Template.genEdit(model,fields)
      println(res)
      fields.foreach(f => res must contain (f._1))
      res must contain(model)
    }

  }


}