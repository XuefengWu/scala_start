package util

import scala.language.experimental.macros
 
import scala.collection.mutable.ArrayBuffer
import scala.reflect.macros.Context
 
object Box {
 
  def apply[T](init: T) = new SimpleBox(init)
 
}
 
abstract class Box[T] {
  private var listeners: ArrayBuffer[Box[_]] = null
 
  def fireChanged() {
    if (listeners != null)
      listeners.foreach(_.onChange(this))
  }
  def onChange(box: Box[_]) {
  }
 
  def addListener(box: Box[_]): this.type = {
    if (listeners == null) {
      listeners = new ArrayBuffer
    }
    listeners += box
    this
  }
 
  def value: T
}
 
class SimpleBox[T](private var v: T) extends Box[T] {
 
  def value_=(value: T) {
    val old = v
    v = value
    if (v != old) fireChanged()
  }
  def value = v
 
  def :=(v: T) {
    value = v
  }
 
}
 
class ExprBox[T](fp: () => T, boxes: Box[_]*) extends Box[T] {
 
  private var v = fp()
 
  boxes.foreach(_.addListener(this))
 
  def value = v
 
  override def onChange(source: Box[_]) {
    v = fp()
  }
}
 
object BindMacro {
 
  def bind[A](expr: A): Box[A] = macro bindImpl[A]
 
  def bindImpl[A](c: Context)(expr: c.Expr[A]): c.Expr[Box[A]] = {
    val boxType = c.universe.typeOf[Box[_]]
    val boxes = for (x @ c.universe.Ident(name) <- expr.tree if x.symbol.typeSignature <:< boxType) yield x
 
    val result = c.universe.reify {
      new ExprBox(() => expr.splice)
    } .tree
    
    val c.universe.Apply(select, args) = result 
    
    val result2 = c.universe.Apply(select, args ++ boxes)
    
    c.Expr(result2)
  }
 
}