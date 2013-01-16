package crud2.macros

import scala.language.reflectiveCalls
import scala.reflect.macros.Context
import language.experimental.macros

object Hello {

  def hello_impl(c: Context)(): c.Expr[String] = {
    import c.universe._
    reify {
      println("Hello Macros!")
      "Hello Macros Comes!"
    }
  }

  def addImpl(c: Context)(a: c.Expr[Int])(b: c.Expr[Int]): c.Expr[Int] = {
    import c.universe._
    val addTree = Apply(
      Select(a.tree, "+"),
      List(b.tree))

    println("addTree: " + addTree)
    c.Expr[Int](addTree)
  }
}