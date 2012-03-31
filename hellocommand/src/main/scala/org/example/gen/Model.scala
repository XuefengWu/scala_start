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

  private def tableModelType(fType:String) = {
    val ft = ScaffoldPlugin.isModelType(fType) match {
      case true =>  {
        val mt = ScaffoldPlugin.extraPureType(fType)
        if(fType.contains("Required")){
          "Long"
        } else {
          "Option[Long]"
        }
      }
      case false => ScaffoldPlugin.extraOptionType(fType)
    }
    ft
  }
  def genCaseClass(m:String,fields:Seq[(String, String)])={
    def buildFields = {
      fields.map{f =>
        val ft = tableModelType(f._2)
        ScaffoldPlugin.modelField(f) +":"+ft
      }.mkString(",")
    }
    //create case class
    """case class %s(id:Pk[Long], %s)
    """.format(m.capitalize,buildFields)
  }
  def genSqlParser(m:String,fields:Seq[(String,String)]) = {
    import ScaffoldPlugin._
    val getParser = fields.map{ f =>
        "get[%s](\"%s.%s\")".format(tableModelType(f._2),m,tableField(f))
    }.mkString(" ~\n\t")
    
    val mapCase = "case id~%s => %s(id, %s)".format(fields.map(modelField(_)).mkString("~"),m.capitalize,fields.map(ScaffoldPlugin.modelField(_)).mkString(","))

    """  val simple = {
      get[Pk[Long]]("%s.id") ~
      %s map {
        %s
      }
    }
    """.format(m,getParser,mapCase)
  }

  def genCreate(m:String,fields:Seq[(String,String)]) = {
    import org.example.ScaffoldPlugin.{tableField,modelField}

    val seq = "(select next value for %s_id_seq)".format(m)
    val columns =fields.map(tableField(_)).mkString(",")
    val setColumns = fields.map("{"+tableField(_)+"}").mkString(",")
    val mapOn = fields.map(f => "'%s -> v.%s".format(tableField(f),modelField(f))).mkString(",\n\t")

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
    import org.example.ScaffoldPlugin.{tableField,modelField}
    val updateSet = fields.map(f => "%s = {%s}".format(tableField(f),tableField(f))).mkString(",")

    val mapOn = fields.map(f => "'%s -> v.%s".format(tableField(f),modelField(f))).mkString(",\n\t")

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
          SQL("select * from %s").as(simple *)
        }
      """.format(m.capitalize,m)

    def genFindById = """
  def findById(id: Long): Option[%s] = { DB.withConnection { implicit c =>
      SQL("select * from %s where  id = {id}").on('id -> id).as(simple.singleOpt)
    }
  }
    """.format(m.capitalize,m)

    def genDelete = """
      def delete(id:Long){
        DB.withConnection{ implicit c =>
          SQL("delete from %s where id = {id}").on(
            'id -> id
          ).executeUpdate()
        }
      }
      """.format(m)

   def genOption = """
         /**
       * Construct the Map[String,String] needed to fill a select options set.
       */
      def options: Seq[(String,String)] = DB.withConnection { implicit connection =>
        SQL("select * from %s").as(%s.simple *).map(c => c.id.toString -> c.toString)
      }
   """.format(m,m.capitalize)

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

    result.append(genOption)

    result.append(genObjectEnd)

    result.toString
  }
}
