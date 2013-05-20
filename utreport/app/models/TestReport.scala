package models

import scala.slick.driver.H2Driver.simple._
import scala.reflect.runtime.universe._
import play.api.db.DB
import play.api.Play.current
import java.sql.Date

case class TestReport(
  id: Option[Long],
  date: Date,
  total: Int,
  fails: Int,
  errors: Int)

// Definition of the SUPPLIERS table
object TestReport extends Table[TestReport]("TestReport") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def date = column[Date]("date")
  def total = column[Int]("total")
  def fails = column[Int]("fails")
  def errors = column[Int]("errors")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ date ~ total ~ fails ~ errors <> (TestReport.apply _, TestReport.unapply _)

  def findAll() = for (s <- TestReport) yield s

  def find(id: Long) =
    for (c <- TestReport if c.id === id) yield c

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1) = {
    findAll().sortBy(_.date.desc).drop(page * pageSize).take(pageSize)
  }

  def autoInc = * returning id

}
 