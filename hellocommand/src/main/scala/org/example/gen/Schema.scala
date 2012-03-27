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

  def gen(model:String)(implicit baseDir:String){

    def genSchema = """
#%s schema

# --- !Ups

CREATE SEQUENCE %s_id_seq;
CREATE TABLE %s (
    id integer NOT NULL DEFAULT nextval('%s_id_seq'),
    title varchar(255)
);


# --- !Downs
DROP TABLE %s;
DROP SEQUENCE %s_id_seq;
    """.format(model.capitalize,model,model,model,model,model)

    val evDir = new File(baseDir+"/conf/evolutions/default")

    if(!evDir.exists())
      evDir.mkdirs()

    //calculate the last evolution number
    val evNum = evDir.list().map(_.dropRight(4).toInt).max + 1
    val sFile = new File(baseDir+"/conf/evolutions/default/%d.sql".format(evNum))
    val out:FileWriter = new FileWriter(sFile)
    out.write(genSchema)
    out.close()
  }
}
