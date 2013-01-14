package models

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

  def get(id: Long)(implicit database: Database): Option[Company] =
    database withSession {
      val result = for (c <- models.Companies if c.id === id) yield c
      result.firstOption
    }

  def update(id: Long, company: Company)(implicit database: Database) =
    database withSession {
      models.Companies.filter(f => f.id === id).update(company)
    }

  def delete(id: Long)(implicit database: Database) =
    database withSession {
      models.Companies.filter(f => f.id === id).delete
    }

  def list(page: Int = 0, pageSize: Int = 5, orderBy: Int = 1, filter:String)(implicit database: Database): Page[Company] =
    {
      val offest = pageSize * page

      val (total, companies) = database withSession {
        val length:Int =  Query(Companies.length).firstOption.getOrElse(0)
        val _companies: Seq[Company] = Query(Companies).filter(c => c.name like s"%$filter%").drop(offest).take(pageSize).list
        (length, _companies)
      }
      Page(companies, page, offest, total)
    }
}