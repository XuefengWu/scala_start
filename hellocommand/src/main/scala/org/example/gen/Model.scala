package org.example.gen

import java.io.FileWriter

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午1:52
 * To change this template use File | Settings | File Templates.
 */

object Model {


  def gen(model:String)(implicit baseDir:String) {
    //create [models].scala


    def genHead = {
      """
package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


    """
    }

    implicit val out:FileWriter = new FileWriter(baseDir+"/app/models/"+model.capitalize+".scala")
    out.write(genHead)
    out.write(genCaseClass(model))
    out.write(genService(model))
    out.close()
  }

  private def genCaseClass(m:String)={
    //create case class
    """
    case class %s(id:Pk[Long], title:String)
    """.format(m.capitalize)
  }

  private def genService(m:String)= {
    def genObjectHead = "object %s {\n".format(m.capitalize)

    def genSqlPareser = """
        val %s = {
        get[Pk[Long]]("id") ~
        get[String]("title") map {
          case id~title => %s(id,title)
        }
      }
      """.format(m,m.capitalize)

    def genList = """
      def list(): List[%s] = DB.withConnection{ implicit c =>
          SQL("select * from %s").as(%s *)
        }
      """.format(m.capitalize,m,m)

    def genCreate = """
        def create(u:%s) {
        DB.withConnection { implicit c =>
          SQL("insert into %s (title) values ({title})").on(
          'title -> u.title
          ).executeUpdate()
        }
      }
      """.format(m.capitalize,m)

    def genFindById = """
  def findById(id: Long) =  DB.withConnection { implicit c =>
      SQL("select * from %s where  id = {id}").on('id -> id).as(%s.singleOpt)
  }
    """.format(m,m)

    def genDelete = """
      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from %s where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      """.format(m)

    def genUpdate = """
          def update(id: Long, u: User) = {

        DB.withConnection { implicit connection =>
          SQL(
            "update user set title = {title} where id = {id}"
          ).on(
            'id -> id,
            'title -> u.title
          ).executeUpdate()
        }
      }
    """.format(m)

    def genObjectEnd = "}"

    val result = new StringBuffer

    result.append(genObjectHead)
    //genSqlPareser
    result.append(genSqlPareser)
    //genMethods
    result.append(genList)

    result.append(genCreate)

    result.append(genFindById)

    result.append(genDelete)

    result.append(genUpdate)

    result.append(genObjectEnd)

    result.toString
  }
}
