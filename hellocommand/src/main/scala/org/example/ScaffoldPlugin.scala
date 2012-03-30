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
    Command.args("gen","<model> <fields=fieldName1:fieldType~fieldName2:fieldType>") { (state,args) =>
      import state._

      implicit val baseDir:String = configuration.baseDirectory.getPath
      print("Thank you very much!\n")
      println("base directory: " + baseDir)
      val model = args.head
      val fieldArg = args.tail.filter(_.contains("fields")).head
      println("fieldArg: "+fieldArg)
      val fieldsValue = fieldArg.split("=").last
      println("fieldsValue: "+fieldsValue)
      val fields = fieldsValue.split("~").map{ f =>
         println("field: "+f)
         val ft = f.split(":")
        (ft.head,ft.last)
      }
      val m = model.toLowerCase
      
      //val fields = List(("name","String"),("lastUpdated","Date"))
      //List(("name","String"),("address","Option[String]"),("phone","Required[String]"))
      val mFile = new File(baseDir+"/app/models/"+m.capitalize+".scala")
      if(mFile.exists()){
        System.out.print(model + " is alread exists.\n do nothing.")
      }else{


        genModel(m,fields)
        genSchema(m,fields)
        genController(m,fields)
        genTemplate(m,fields)
        genRouters(m,fields)
      }
      state
    }

  private def genTemplate(model:String,fields:Seq[(String, String)])(implicit baseDir:String) {

    //check app/view/[model] folder
    val tDirPath = "%s/app/views/%s".format(baseDir,model)
    val tDir = new File(tDirPath)

    if(!tDir.exists())
      tDir.mkdirs()


    val outList:FileWriter = new FileWriter(tDirPath+"/list.scala.html")
    outList.write(Template.genList(model,fields))
    outList.close()

    val outEdit:FileWriter = new FileWriter(tDirPath+"/edit.scala.html")
    outEdit.write(Template.genEdit(model,fields))
    outEdit.close()

    val outCreate:FileWriter = new FileWriter(tDirPath+"/create.scala.html")
    outCreate.write(Template.genCreate(model,fields))
    outCreate.close()
  }

  private def genRouters(model:String,fields:Seq[(String, String)])(implicit baseDir:String){
    val out = new FileWriter(baseDir+"/conf/routes",true)
    out.write(Routers.gen(model))
    out.close()
  }

  private def genController(model:String,fields:Seq[(String, String)])(implicit baseDir:String){
    
    //check app/controllers folder
    val evDir = new File(baseDir + "/app/controllers")

    if (!evDir.exists())
      evDir.mkdir()
    //create [model]s.scala
    val cFile = new File(baseDir + "/app/controllers/%ss.scala".format(model.capitalize))
    if (cFile.exists())
      System.out.print(model + " is alread exists")

    implicit val out: FileWriter = new FileWriter(cFile) 
    out.write(Controller.gen(model,fields))
    out.close()
  }
  
  private def genModel(model:String,fields:Seq[(String, String)])(implicit baseDir:String){
    val out:FileWriter = new FileWriter(baseDir+"/app/models/"+model.capitalize+".scala")
    out.write(Model.gen(model,fields))
    out.close()
  }
  
  private def genSchema(model:String,fields:Seq[(String, String)])(implicit baseDir:String) {
    val evDir = new File(baseDir+"/conf/evolutions/default")

    if(!evDir.exists())
      evDir.mkdirs()

    //calculate the last evolution number
    val evNum = evDir.list().map(_.dropRight(4).toInt).max + 1
    val sFile = new File(baseDir+"/conf/evolutions/default/%d.sql".format(evNum))
    val out:FileWriter = new FileWriter(sFile)
    out.write(Schema.gen(model,fields))
    out.close()
  }
}
