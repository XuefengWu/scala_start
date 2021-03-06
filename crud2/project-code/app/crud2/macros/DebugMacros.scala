package crud2.macros


import language.experimental.macros

import reflect.macros.Context

object DebugMacros {

  //def debug1(param: Any): Unit = macro debug1_impl

  def debug1_impl(c: Context)(param: c.Expr[Any]): c.Expr[Unit] = {
    import c.universe._
    val paramRep = show(param.tree)
    val paramRepTree = Literal(Constant(paramRep))
    val paramRepExpr = c.Expr[String](paramRepTree)
    reify { println(paramRepExpr.splice + " = " + param.splice) }
  }

  //def debug(params: Any*): Unit = macro debug_impl

  def debug_impl(c: Context)(params: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    val trees = params.map { param =>
      param.tree match {
        // Keeping constants as-is
        // The c.universe prefixes aren't necessary, but otherwise Idea keeps importing weird stuff ...
        case c.universe.Literal(c.universe.Constant(const)) => {
          val reified = reify { print(param.splice) }
          reified.tree
        }
        case _ => {
          val paramRep = show(param.tree)
          val paramRepTree = Literal(Constant(paramRep))
          val paramRepExpr = c.Expr[String](paramRepTree)
          val reified = reify { print(paramRepExpr.splice + " = " + param.splice) }
          reified.tree
        }
      }
    }

    // Inserting ", " between trees, and a println at the end.
    val separators = (1 to trees.size-1).map(_ => (reify { print(", ") }).tree) :+ (reify { println() }).tree
    val treesWithSeparators = trees.zip(separators).flatMap(p => List(p._1, p._2))

    c.Expr[Unit](Block(treesWithSeparators.toList, Literal(Constant(()))))
  }
}