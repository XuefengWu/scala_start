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
    out.write("case class %s(id:Long, title:String)\n".format(m.capitalize))

    out.write("""
    case class %s(id:Long, title:String) {
      def save() = {
        if(id > 0){
          DB.withConnection { implicit c =>
            SQL("update %s set label = {label} where id = {id}")
              .on('title -> title,'id -> id).executeUpdate()
          }
        } else {
          Task.create(label)
        }
      }
    }
    """.format(m.capitalize,m))
  }

  private def genService(m:String)(implicit out:FileWriter) {
    out.write("object %s {\n".format(m.capitalize))
    //genSqlPareser
    out.write("""
        val %s = {
        get[Long]("id") ~
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
        def create(title:String) {
        DB.withConnection { implicit c =>
          SQL("insert into %s (title) values ({title})").on(
          'title -> title
          ).executeUpdate()
        }
      }
      """.format(m))

    out.write("""
  def find(id: Long) =  DB.withConnection { implicit c =>
      SQL("select * from %s where {id}").on('id -> id).single(%s)
  }
    """.format(m))

    out.write("""
      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from %s where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      """.format(m))
    out.write("}")
  }
}
