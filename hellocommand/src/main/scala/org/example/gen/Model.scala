package org.example.gen

import java.io.FileWriter
import org.example.ScaffoldPlugin
import org.stringtemplate.v4.ST

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午1:52
 * To change this template use File | Settings | File Templates.
 */

object Model {

  import ScaffoldPlugin.{hasReferenceModel,extraPureType,tableField,modelField,isModelType,fieldsWithModel,caseClassWithReference}

  def gen(model:String,fields:Seq[(String, String)])= {
    //create [models].scala


    val result = new StringBuffer()
    result.append(genHead(fields))
    result.append(genPageHead)
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
  private def genPageHead = {
    """
/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}
    """
  }
  private def tableModelType(fType:String) = {
    val ft = isModelType(fType) match {
      case true =>  {
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
    """case class %s(id:Pk[Long] = NotAssigned, %s)
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


  def genSqlParserWithReference(m:String,fields:Seq[(String,String)]):String = {
    
    val modelParser = fieldsWithModel(m,fields).map{ f  =>
      val fm = f._2
      if(fm.contains("Required")){
        extraPureType(fm)+".simple"
      }else{
        "(%s.simple ?)".format(fm)
      }  
    }.mkString(" ~ ")
    
    val caseFields = {
      val ps = fieldsWithModel(m,fields).map(_._1).mkString("~")
      val rs = fieldsWithModel(m,fields).map(_._1).mkString("(",",",")")
      " %s => %s".format(ps,rs)
    }

   val res =
    """
    /**
     * Parse a %s from a ResultSet
     */
    val withReference = %s map {
      case %s
    }
    """.format(caseClassWithReference(m,fields),modelParser,caseFields)
   res
  }
  def genCreate(m:String,fields:Seq[(String,String)]) = {
    import org.example.ScaffoldPlugin.{tableField,modelField}

    val columns =fields.map(tableField(_)).mkString(",")
    val setColumns = fields.map("{"+tableField(_)+"}").mkString(",")
    val mapOn = fields.map(f => "'%s -> v.%s".format(tableField(f),modelField(f))).mkString(",\n\t")

    val res =
    """

    def create(v:%s) {
      DB.withConnection { implicit connection =>
        SQL("insert into %s (%s) values (%s)").on(
        %s
        ).executeUpdate()
      }
    }
    """.format(m.capitalize,m,columns,setColumns,mapOn)

    res
  }

  def genUpdate(m:String,fields:Seq[(String,String)]) = {

    val updateSet = fields.map(f => "%s = {%s}".format(tableField(f),tableField(f))).mkString(",")

    val mapOn = fields.map(f => "'%s -> v.%s".format(tableField(f),modelField(f))).mkString(",\n\t")

    val res =
    """

    def update(id: Long, v: %s) = {
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
    res
  }

  private def genService(m:String,fields:Seq[(String,String)])= {
    def genObjectHead = "object %s {\n".format(m.capitalize)

    def genList = {
      
      def filterCondition = {
        val stringFields = fields.filter(_._2.contains("String"))
        if(stringFields.isEmpty){
          " 1 = 1 "
        } else {
          stringFields.map(f => "%s.%s like {filter}".format(m,f._1)).mkString("("," or ",")")
        }
      }
      val ins = getClass.getResourceAsStream("/model/list.st")
      val lines = scala.io.Source.fromInputStream(ins).mkString
      val sqlParser = {
        hasReferenceModel(fields) match {
          case true =>  "withReference"
          case false => "simple"
        }
      }
      val join = {
        fieldsWithModel(m,fields).tail.map{ f =>
          val jst = new ST("left join <rm> on <m>.<rf> = <rm>.id")
          jst.add("rm",f._2.toLowerCase)
          jst.add("m",m)
          jst.add("rf",tableField(f))
          jst.render()
        }.mkString("\n")
      }
      val st = new ST(lines)
      st.add("m", m)
      st.add("MM", m.capitalize)
      st.add("caseClassWithReference", caseClassWithReference(m,fields))
      st.add("filterCondition", filterCondition)
      st.add("sqlParser",sqlParser)
      if(hasReferenceModel(fields)){

        val joinField = fieldsWithModel(m,fields).tail.map(f => {
          "left join %s on %s.%s = %s.id".format(extraPureType(f._2),m.toLowerCase,tableField(f),extraPureType(f._2).toLowerCase)
        }).mkString("\n")
        st.add("join",joinField)
      }
      st.render()  
    }

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

    if(hasReferenceModel(fields)){
      result.append(genSqlParserWithReference(m,fields))
    }
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
