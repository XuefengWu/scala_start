package models

import scala.slick.driver.H2Driver.simple._


case class Bar(id: Option[Int] = None, name: String)

object Bars extends Table[Bar]("bar") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  // This is the primary key column
  def name = column[String]("name")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ name <>(Bar, Bar.unapply _)
}