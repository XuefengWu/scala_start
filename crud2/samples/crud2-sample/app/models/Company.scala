package models

import language.experimental.macros

import scala.slick.driver.H2Driver.simple._
import scala.slick.session.Session

// Use the implicit threadLocalSession
import Database.threadLocalSession

case class Company(id: Option[Long] = None, name: String)

object Companies extends Table[Company]("company") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  // This is the primary key column
  def name = column[String]("name")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ name <> (Company, Company.unapply _)

  def getSimple(id: Long)(implicit database: Database): Option[Company] = database.withSession(scala.slick.lifted.Query.apply(this).take(1).firstOption())

  def get(id: Long)(implicit database: Database): Option[Company] = //macro crud2.macros.Models.getImpl[Option[Company]]
        database.withSession(Query(this).filter(_.id === id).take(1).firstOption)
      
  def update(id: Long, company: Company)(implicit database: Database) =
    database withSession {
      Query(this).filter(f => f.id === id).update(company)
    }

  def delete(id: Long)(implicit database: Database) =
    database withSession {
      Query(this).filter(f => f.id === id).delete
    }
 
}