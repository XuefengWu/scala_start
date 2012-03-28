package org.example.gen

import org.specs2.mutable._

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-28
 * Time: 下午12:54
 * To change this template use File | Settings | File Templates.
 */

class ControllerSpec extends SpecificationWithJUnit  {
  val fields = List(("name","String"),("lastUpdated","DateTime"))
  val model = "todo"

  "gen form " should {
    "start with Form name" in {
      val res = Controller.genForm(model,fields)
      println(res)
      res must contain("%sForm".format(model))
      res must contain("name")
    }

  }
}
