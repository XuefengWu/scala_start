package org.example


import sbt._
import Keys._
import java.io.FileWriter
import org.example.gen._

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-15
 * Time: 下午3:54
 * To change this template use File | Settings | File Templates.
 */

object ScaffoldPlugin extends Plugin {

  override lazy val settings = Seq(commands += genCommand)

  lazy val genCommand =
    Command.single("gen") { (state: State, model: String) =>
      import state._

      implicit val baseDir:String = configuration.baseDirectory.getPath
      print("Thank you very much!\n")
      println("base directory: " + baseDir)

      val m = model.toLowerCase
      val mFile = new File(baseDir+"/app/models/"+m.capitalize+".scala")
      if(mFile.exists()){
        System.out.print(model + " is alread exists.\n do nothing.")
      }else{
        Model.gen(m)
        Schema.gen(m)
        Controller.gen(m)
        Template.gen(m)
        Routers.gen(m)
      }
      state
    }

}
