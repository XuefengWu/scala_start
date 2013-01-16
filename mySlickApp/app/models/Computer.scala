package models

import java.sql.Date
import scala.slick.driver.H2Driver.simple._
import scala.slick.session.Session

// Use the implicit threadLocalSession
import Database.threadLocalSession


/**
 * Created with IntelliJ IDEA.
 * User: Orna
 * Date: 13-1-14
 * Time: 下午9:22
 * To change this template use File | Settings | File Templates.
 */

case class Computer(id: Option[Long] = None, name: String, introduced: Option[Date] = None, discontinued: Option[Date] = None, companyId: Option[Long])


object Computers extends Table[Computer]("computer") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  // This is the primary key column
  def name = column[String]("name")

  def introduced = column[Option[Date]]("introduced", O.Nullable)

  def discontinued = column[Option[Date]]("discontinued", O.Nullable)

  def companyId =  column[Option[Long]]("companyId", O.Nullable)
  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ name ~ introduced ~ discontinued ~ companyId   <> (Computer, Computer.unapply _)

  def companyFK = foreignKey("company_fk", companyId, Companies)(_.id)
  def companyJoin = Companies.where(_.id === companyId)

  def get(id: Long)(implicit database: Database): Option[Computer] =
    database withSession {
      val result = for (c <- models.Computers if c.id === id) yield c
      result.firstOption
    }

  def update(id: Long, computer: Computer)(implicit database: Database) =
    database withSession {
      models.Computers.filter(f => f.id === id).update(computer)
    }

  def delete(id: Long)(implicit database: Database) =
    database withSession {
      models.Computers.filter(f => f.id === id).delete
    }

  def list(page: Int = 0, pageSize: Int = 5, orderBy: Int = 1, filter:String)(implicit database: Database): Page[Computer] =
  {
    val offest = pageSize * page

    val (total, computers) = database withSession {
      val length:Int =  Query(Computers.length).firstOption.getOrElse(0)
      val _computers: Seq[Computer] = Query(Computers).filter(c => c.name like s"%$filter%").drop(offest).take(pageSize).list
      (length, _computers)
    }
    Page(computers, page, offest, total)
  }

}