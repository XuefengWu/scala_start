package org.example.gen

import java.io.{FileWriter, File}

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
      case "Date" => "%s timestamp".format(f._1)
      case _ => "%s varchar(255)".format(f._1)
    }

  }.mkString(",\n\t")
    
"""#%s schema

# --- !Ups

CREATE SEQUENCE %s_id_seq;
CREATE TABLE %s (
    id integer NOT NULL DEFAULT nextval('%s_id_seq'),
    %s
);


# --- !Downs
DROP TABLE %s;
DROP SEQUENCE %s_id_seq;
    """.format(model.capitalize,model,model,model,columns,model,model)
  }
}