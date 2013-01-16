package crud2.controllers

import language.experimental.macros
/**
 * Created with IntelliJ IDEA.
 * User: Orna
 * Date: 13-1-15
 * Time: 下午8:48
 * To change this template use File | Settings | File Templates.
 */
object Hello {

  def printparam(format:String,params: Any*): Unit = macro crud2.macros.PrintfMacros.printf_impl

  def hello() = macro crud2.macros.Hello.hello_impl

  def get[T](id: Long)(implicit database: scala.slick.session.Database)  = macro crud2.macros.Models.getImpl[T]
  
  def add(a:Int)(implicit b:Int):Int = macro crud2.macros.Hello.addImpl

}
