package crud2.models

import language.experimental.macros
import scala.slick.driver.H2Driver.simple._
import scala.slick.session.Session
// Use the implicit threadLocalSession
import Database.threadLocalSession
/**
 * Created with IntelliJ IDEA.
 * User: Orna
 * Date: 13-1-15
 * Time: 下午9:00
 * To change this template use File | Settings | File Templates.
 */
trait CRUD2[T] extends Table[T] {
  def get(id: Long)(implicit database: scala.slick.session.Database) = macro crud2.macros.Models.getImpl[Option[T]] 
    

  def getHard(id: Long)(implicit database: scala.slick.session.Database) =  
    database.withSession(scala.slick.lifted.Query.apply(this).take(1).firstOption())
}
