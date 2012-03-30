package org.example.gen

import java.io.FileWriter
import org.example.ScaffoldPlugin

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午1:52
 * To change this template use File | Settings | File Templates.
 */

object Model {

  def gen(model:String,fields:Seq[(String, String)])= {
    //create [models].scala


    val result = new StringBuffer()
    result.append(genHead(fields))
    result.append(genCaseClass(model,fields))
    result.append(genService(model,fields))
    result.toString
  }
  private  def genHead(fields:Seq[(String, String)]) = {

    val importTypes = fields.map{ f =>
      ScaffoldPlugin.extraPureType(f._2) match {
        case "String" => ""
        case "Long" => ""
        case "Integer" => ""
        case "Boolean" => ""
        case "BigInteger" => "import java.math.BigInteger"
        case "Float" => ""
        case "Double" => ""
        case "BigDecimal" => "import java.math.BigDecimal"
        case "Time" => "import java.sql.Time"
        case "Timestamp" => "import java.sql.Timestamp"
        case "Date" => "import java.util.Date"
        case _ => ""
      }
    }.toSet.mkString("\n")

    val head =
    """package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current
%s

    """.format(importTypes)
    head
  }

  def genCaseClass(m:String,fields:Seq[(String, String)])={
    //create case class
    """case class %s(id:Pk[Long], %s)
    """.format(m.capitalize,fields.map(f => f._1+":"+ScaffoldPlugin.extraOptionType(f._2)).mkString(","))
  }

  def genSqlParser(m:String,fields:Seq[(String,String)]) = {
    val getParser = fields.map{ f =>
       "get[%s](\"%s.%s\")".format(ScaffoldPlugin.extraOptionType(f._2),m,f._1)
    }.mkString(" ~\n\t")
    val mapCase = "case id~%s => %s(id, %s)".format(fields.map(_._1).mkString("~"),m.capitalize,fields.map(_._1).mkString(","))
    """  val %s = {
      get[Pk[Long]]("%s.id") ~
      %s map {
        %s
      }
    }
    """.format(m,m,getParser,mapCase)
  }

  def genCreate(m:String,fields:Seq[(String,String)]) = {
    val fieldsName = fields.map(_._1)

    val seq = "(select next value for %s_id_seq)".format(m)
    val columns =fieldsName.mkString(",")
    val setColumns = fieldsName.map("{"+_+"}").mkString(",")
    val mapOn = fieldsName.map(f => "'%s -> v.%s".format(f,f)).mkString(",\n\t")

    """    def create(v:%s) {
      DB.withConnection { implicit connection =>
        SQL("insert into %s (id,%s) values (%s,%s)").on(
        %s
        ).executeUpdate()
      }
    }
    """.format(m.capitalize,m,columns,seq,setColumns,mapOn)
  }

  def genUpdate(m:String,fields:Seq[(String,String)]) = {
    val fieldsName = fields.map(_._1)
    val updateSet = fieldsName.map(f => "%s = {%s}".format(f,f)).mkString(",")

    val mapOn = fieldsName.map(f => "'%s -> v.%s".format(f,f)).mkString(",\n\t")

    """   def update(id: Long, v: %s) = {
        DB.withConnection { implicit connection =>
          SQL(
            "update %s set %s where id = {id}"
          ).on(
            'id -> id,
            %s
          ).executeUpdate()
        }
      }
    """.format(m.capitalize,m,updateSet,mapOn)
  }

  private def genService(m:String,fields:Seq[(String,String)])= {
    def genObjectHead = "object %s {\n".format(m.capitalize)

    def genList = """
      def list(): List[%s] = DB.withConnection{ implicit c =>
          SQL("select * from %s").as(%s *)
        }
      """.format(m.capitalize,m,m)

    def genFindById = """
  def findById(id: Long): Option[%s] = { DB.withConnection { implicit c =>
      SQL("select * from %s where  id = {id}").on('id -> id).as(%s.singleOpt)
    }
  }
    """.format(m.capitalize,m,m)

    def genDelete = """
      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from %s where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      """.format(m)



    def genObjectEnd = "}"

    val result = new StringBuffer

    result.append(genObjectHead)
    //genSqlPareser
    result.append(genSqlParser(m,fields))
    //genMethods
    result.append(genList)

    result.append(genCreate(m,fields))

    result.append(genFindById)

    result.append(genDelete)

    result.append(genUpdate(m,fields))

    result.append(genObjectEnd)

    result.toString
  }
}
