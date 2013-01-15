package crud2.macros

import language.experimental.macros

import reflect.macros.Context

object Hello {

  def hello_impl(c: Context)(): c.Expr[String] = {
    import c.universe._
    reify { 
      println("Hello Macros!") 
      "Hello Macros Comes!"
    }
  }
    
}