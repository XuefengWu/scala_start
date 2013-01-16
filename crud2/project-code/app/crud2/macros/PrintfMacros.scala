package crud2.macros

import language.experimental.macros
import scala.reflect.macros.Context
import scala.collection.mutable.{ ListBuffer, Stack }

object PrintfMacros {

  def printparam(format:String,params: Any*): Unit = macro printf_impl

  
  //macro definition
  def printf_impl(c: Context)(format: c.Expr[String], params: c.Expr[Any]*): c.Expr[Unit] = {
    //it includes a lot of routinely used functions and types
    import c.universe._
    //parse the provided format string.
    val Literal(Constant(s_format: String)) = format.tree
    val evals = ListBuffer[ValDef]()
    def precompute(value: Tree, tpe: Type): Ident = {
      val freshName = newTermName(c.fresh("eval$"))
      evals += ValDef(Modifiers(), freshName, TypeTree(tpe), value)
      Ident(freshName)
    }
    val paramsStack = Stack[Tree]((params map (_.tree)): _*)
    val refs = s_format.split("(?<=%[\\w%])|(?=%[\\w%])") map {
      case "%d" => precompute(paramsStack.pop, typeOf[Int])
      case "%s" => precompute(paramsStack.pop, typeOf[String])
      case "%%" => Literal(Constant("%"))
      case part => Literal(Constant(part))
    }
    // create ASTs (abstract syntax trees) which represent Scala code. 
    // reify provides a shortcut for creating ASTs
    val stats = evals ++ refs.map(ref => reify(print(c.Expr[Any](ref).splice)).tree)
    println("stats:\n"+stats)
    // combines all the generated code into a Block
    c.Expr[Unit](Block(stats.toList, Literal(Constant(()))))
  }
}