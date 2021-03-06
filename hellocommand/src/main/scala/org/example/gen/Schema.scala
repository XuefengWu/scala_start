package org.example.gen

import java.io.{FileWriter, File}
import org.example.ScaffoldPlugin

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午4:25
 * To change this template use File | Settings | File Templates.
 */

object Schema {

  def gen(model:String,fields:Seq[(String, String)]) = {

  val columns = fields.map{f => 
    f._2 match {
      case t if ScaffoldPlugin.isModelType(t) => "%s bigint".format(ScaffoldPlugin.tableField(f))
      case "String" => "%s varchar(255)".format(f._1)
      case "Option[String]" => "%s varchar(255)".format(f._1)
      case "Required[String]" => "%s varchar(255) NOT NULL".format(f._1)
      case "Long" => "%s INTEGER".format(f._1)
      case "Option[Long]" => "%s INTEGER".format(f._1)
      case "Required[Long]" => "%s INTEGER NOT NULL".format(f._1)
      case "Integer" => "%s TINYINT".format(f._1)
      case "Option[Integer]" => "%s TINYINT".format(f._1)
      case "Required[Integer]" => "%s TINYINT NOT NULL".format(f._1)
      case "Boolean" => "%s BIT".format(f._1)
      case "Option[Boolean]" => "%s BIT".format(f._1)
      case "Required[Boolean]" => "%s BIT NOT NULL".format(f._1)
      case "BigInteger" => "%s BIGINT".format(f._1)
      case "Option[BigInteger]" => "%s BIGINT".format(f._1)
      case "Required[BigInteger]" => "%s BIGINT NOT NULL".format(f._1)
      case "Float" => "%s FLOAT".format(f._1)
      case "Option[Float]" => "%s FLOAT".format(f._1)
      case "Required[Float]" => "%s FLOAT NOT NULL".format(f._1)
      case "Double" => "%s DOUBLE".format(f._1)
      case "Option[Double]" => "%s DOUBLE".format(f._1)
      case "Required[Double]" => "%s DOUBLE NOT NULL".format(f._1)
      case "BigDecimal" => "%s DECIMAL".format(f._1)
      case "Option[BigDecimal]" => "%s DECIMAL".format(f._1)
      case "Required[BigDecimal]" => "%s DECIMAL NOT NULL".format(f._1)
      case "Time" => "%s TIME".format(f._1)
      case "Option[Time]" => "%s TIME".format(f._1)
      case "Required[Time]" => "%s TIME NOT NULL".format(f._1)
      case "Timestamp" => "%s TIMESTAMP".format(f._1)
      case "Option[Timestamp]" => "%s TIMESTAMP".format(f._1)
      case "Required[Timestamp]" => "%s TIMESTAMP NOT NULL".format(f._1)
      case "Date" => "%s DATE".format(f._1)
      case "Option[Date]" => "%s DATE".format(f._1)
      case "Required[Date]" => "%s DATE NOT NULL".format(f._1)
      case _ => "%s varchar(255)".format(f._1)
    }

  }.mkString(",\n\t")
    
"""#%s schema

# --- !Ups

CREATE TABLE %s (
    id bigint NOT NULL auto_increment,
    %s
    ,constraint pk_%s primary key (id)
);


# --- !Downs
DROP TABLE %s;
    """.format(model.capitalize,model,columns,model,model)

  }
}