package models

import scala.slick.driver.MySQLDriver.simple._
import scala.reflect.runtime.universe._
import play.api.db.DB
import play.api.Play.current


case class TestCase(
  id: Option[Long],
  reportId:Long,
  name: String,
  classname: String,
  time: Double,
  message: String,
  typ: String)

case class TestCaseDiff(name:String, count:Int)

// Definition of the SUPPLIERS table
object TestCases extends Table[TestCase]("TestCase") {
  
  
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def name = column[String]("name")
  def classname = column[String]("classname")
  def time = column[Double]("time")
  def message = column[String]("message")
  def typ = column[String]("typ")
  def reportId = column[Long]("reportId") 
  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ reportId ~ name ~ classname ~ time ~ message ~ typ <> (TestCase.apply _, TestCase.unapply _)

  def findAll(reportId:Long) = for (s <- TestCases if s.reportId === reportId) yield s

  def find(id: Long) =
    for (c <- TestCases if c.id === id) yield c

  def list(reportId:Long,page: Int = 0, pageSize: Int = 10, orderBy: Int = 1) = {
    findAll(reportId).sortBy(_.typ).drop(page * pageSize).take(pageSize)
  }

  def delete(reportId:Long)(implicit session:Session){
    println(findAll(reportId).deleteStatement)
    findAll(reportId).delete
  }
}