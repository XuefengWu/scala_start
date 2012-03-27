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
    implicit val out:FileWriter = new FileWriter(baseDir+"/app/models/"+model.capitalize+".scala")
    out.write("""
package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current


    """)
    genCaseClass(model)
    genService(model)
    out.close()
  }

  private def genCaseClass(m:String)(implicit out:FileWriter){
    //create case class

    out.write("""
    case class %s(id:Pk[Long], title:String)
    """.format(m.capitalize))
  }

  private def genService(m:String)(implicit out:FileWriter) {
    out.write("object %s {\n".format(m.capitalize))
    //genSqlPareser
    out.write("""
        val %s = {
        get[Pk[Long]]("id") ~
        get[String]("title") map {
          case id~title => %s(id,title)
        }
      }
      """.format(m,m.capitalize))

    //genMethods
    out.write("""
      def list(): List[%s] = DB.withConnection{ implicit c =>
          SQL("select * from %s").as(%s *)
        }
      """.format(m.capitalize,m,m))

    out.write("""
        def create(u:%s) {
        DB.withConnection { implicit c =>
          SQL("insert into %s (title) values ({title})").on(
          'title -> u.title
          ).executeUpdate()
        }
      }
      """.format(m.capitalize,m))

    out.write("""
  def findById(id: Long) =  DB.withConnection { implicit c =>
      SQL("select * from %s where  id = {id}").on('id -> id).as(%s.singleOpt)
  }
    """.format(m,m))

    out.write("""
      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from %s where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      """.format(m))

    out.write("""
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
    """.format(m))

    out.write("}")
  }
}
