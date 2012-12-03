package org.example.gen

import org.specs2.mutable._

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-28
 * Time: 下午2:22
 * To change this template use File | Settings | File Templates.
 */

class ModelSpec extends SpecificationWithJUnit  {
  val fields = List(("name","String"),("lastUpdated","DateTime"))
  val model = "todo"

  "gen CaseClass " should {
    "start with case class model" in {
      val res = Model.genCaseClass(model,Nil)
      println(res)
      res must startWith("case class %s".format(model.capitalize))
    }
    "with field: name" in {
      val res = Model.genCaseClass(model,fields)
      println(res)
      res must contain ("name:String")
    }

  }

  "gen Sql Pareser " should {
    "start with user" in {
      val res = Model.genSqlParser(model,Nil)
      println(res)
      res must contain("val %s".format(model))
    }
    "with field: name" in {
      val res = Model.genSqlParser(model,fields)
      println(res)
      res must contain ("name")
    }
  }

  "gen Create " should {
    "with field: name" in {
      val res = Model.genCreate(model,fields)
      println(res)
      res must contain ("name")
    }
  }

  "gen Update " should {
    "with field: name" in {
      val res = Model.genUpdate(model,fields)
      println(res)
      res must contain ("name")
    }
  }

  "gen Model " should {
    "with field: name" in {
      val res = Model.gen(model,fields)
      println(res)
      res must contain ("name")
    }
  }
}
