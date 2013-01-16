package crud2.macros

import language.experimental.macros

import reflect.macros.Context
/**
 * Created with IntelliJ IDEA.
 * User: Orna
 * Date: 13-1-15
 * Time: 下午8:56
 * To change this template use File | Settings | File Templates.
 */
object Models {


  def getImpl[A : c.WeakTypeTag](c: Context): c.Expr[A] = {
    import c.universe._
    val companioned = weakTypeOf[A].typeSymbol
    val companionSymbol = companioned.companionSymbol
    val companionType = companionSymbol.typeSignature

    companionType.declaration(stringToTermName("unapply")) match {
      case NoSymbol => c.abort(c.enclosingPosition, "No unapply function found")
      case s =>
        val unapply = s.asMethod
        val unapplyReturnTypes = unapply.returnType match {
          case TypeRef(_, _, args) =>
            args.head match {
              case t @ TypeRef(_, _, Nil) => Some(List(t))
              case TypeRef(_, _, args) => Some(args)
              case _ => None
            }
          case _ => None
        }
        //println("Unapply return type:" + unapplyReturnTypes)

        companionType.declaration(stringToTermName("apply")) match {
          case NoSymbol => c.abort(c.enclosingPosition, "No apply function found")
          case s =>
            // searches apply method corresponding to unapply
            val applies = s.asMethod.alternatives
            val apply = applies.collectFirst{
              case (apply: MethodSymbol) if(apply.paramss.headOption.map(_.map(_.asTerm.typeSignature)) == unapplyReturnTypes) => apply
            }
            apply match {
              case Some(apply) => {
                //println("apply found:" + apply)
                val params = apply.paramss.head //verify there is a single parameter group

                val inferedImplicits = params.map(_.typeSignature).map{ implType =>

                  val (isRecursive, tpe) = implType match {
                    case TypeRef(_, t, args) =>
                      // Option[_] needs special treatment because we need to use XXXOpt
                      if(implType.typeConstructor <:< typeOf[Option[_]].typeConstructor)
                        (args.exists{ a => a.typeSymbol == companioned }, args.head)
                      else (args.exists{ a => a.typeSymbol == companioned }, implType)
                    case TypeRef(_, t, _) =>
                      (false, implType)
                  }
                  (implType, isRecursive, tpe)
                }

                inferedImplicits.collect { case (t, rec, _) if(rec) => t }match {
                  case List() => {
                    val namedImplicits = params.map(_.name).zip(inferedImplicits)
                    //println("Found implicits:"+namedImplicits)

                    val helperMember = Select( This(tpnme.EMPTY), "lazyStuff")

                    var hasRec = false

                    // combines all reads into CanBuildX
                    val values = namedImplicits.map {
                      case (name, (t, rec, tpe)) =>
                        // inception of "name"
                        Literal(Constant(name.decoded))
                    }

                    // builds the final Reads using apply method
                    val applyType = Ident( companionSymbol.name )

                    // if case class has one single field, needs to use inmap instead of canbuild.apply
                    val finalTree =
                      Apply(
                        Select(applyType, "apply"),
                        values
                      )

                    println("finalTree: "+finalTree)
                    c.Expr[A](finalTree)
                  }
                  case l => c.abort(c.enclosingPosition, s"No implicit Reads for ${l.mkString(", ")} available.")
                }
              }
              case None => c.abort(c.enclosingPosition, "No apply function found matching unapply parameters")
            }
         //end:  companionType.declaration(stringToTermName("apply")) match
        }
      //end: companionType.declaration
    }
  }
}
