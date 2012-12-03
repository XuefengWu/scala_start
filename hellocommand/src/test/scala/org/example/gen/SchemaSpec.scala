package org.example.gen

import org.specs2.mutable._

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-28
 * Time: 下午3:38
 * To change this template use File | Settings | File Templates.
 */

class SchemaSpec extends SpecificationWithJUnit  {
  val fields = List(("name","String"),("lastUpdated","DateTime"))
  val model = "todo"

  "gen Schema " should {
    "start with  name" in {
      val res = Schema.gen(model,fields)
      println(res)
      res must contain("name")
    }

  }
}