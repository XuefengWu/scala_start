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

  override lazy val settings = Seq(commands ++= Seq(genCommand,genRemoveCommand))


  
  lazy val genRemoveCommand = Command.single("gen-remove") { (state: State, model: String) =>

    import state._

    implicit val baseDir:String = configuration.baseDirectory.getPath
    println("Remove: " + model)
    val tDirPath = "%s/app/views/%s".format(baseDir,model)
    val tDir = new File(tDirPath)
    if(tDir.exists()){
      tDir.listFiles().foreach(_.delete())
      tDir.delete() 
    }
    
    val cFile = new File(baseDir + "/app/controllers/%ss.scala".format(model.capitalize))
    if(cFile.exists()) {
      cFile.delete()  
    }
    
    val mFile = new File(baseDir+"/app/models/"+model.capitalize+".scala")
    if(mFile.exists()){
      mFile.delete()
    }
    state
  }
  
  lazy val genCommand =
    Command.args("gen-crud","<model> <fields=fieldName1:fieldType~fieldName2:fieldType>") { (state,args) =>
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
      val invalidateFieldTypes = invalidateFieldType(fields.map(_._2))
      if(mFile.exists() || invalidateFieldTypes.size > 0){
        System.out.println(model + " is alread exists.\n do nothing.")
        System.out.println("or field Type is not validate: " + invalidateFieldTypes.mkString(","))
      }else{
        genModel(m,fields)
        genSchema(m,fields)
        genController(m,fields)
        genTemplate(m,fields)
        genRouters(m,fields)
      }
      state
    }



  private def invalidateFieldType(fTypes:Seq[String]):Seq[String] = {
     val javaType = List("String","Long","Integer","Boolean","BigInteger","Float","Double","BigDecimal","Time","Timestamp","Date")
    fTypes.filterNot(ft => javaType.contains(extraPureType(ft)) || isModelType(ft))
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
    val modelsDir = new File(baseDir+"/app/models")
    if(!modelsDir.exists()){
      modelsDir.mkdirs()
    }
    
    val out:FileWriter = new FileWriter(baseDir+"/app/models/"+model.capitalize+".scala")
    out.write(Model.gen(model,fields))
    out.close()
  }
  
  private def genSchema(model:String,fields:Seq[(String, String)])(implicit baseDir:String) {
    val evDir = new File(baseDir+"/conf/evolutions/default")

    if(!evDir.exists())
      evDir.mkdirs()

    //calculate the last evolution number
    
    val evNum = evDir.list().isEmpty match {
      case true => 1
      case false => evDir.list().map(_.dropRight(4).toInt).max + 1
    }

    val sFile = new File(baseDir+"/conf/evolutions/default/%d.sql".format(evNum))
    val out:FileWriter = new FileWriter(sFile)
    out.write(Schema.gen(model,fields))
    out.close()
  }


  def extraPureType(fType:String):String = {
    val OT = """Option\[([a-zA-Z]*)\]""".r
    val RT = """Required\[([a-zA-Z]*)\]""".r
    fType match {
      case OT(t) => t
      case RT(t) => t
      case t => t
    }
  }

  def extraOptionType(fType:String):String = {
    val OT = """Option\[([a-zA-Z]*)\]""".r
    val RT = """Required\[([a-zA-Z]*)\]""".r
    fType match {
      case RT(t) => t
      case OT(t) => "Option[%s]".format(t)
      case t => "Option[%s]".format(t)
    }
  }


  def isModelType(fType:String) = {
    val modelTypes = new File("app/models").list().map(_.dropRight(6))
    modelTypes.contains(extraPureType(fType))
  }


  def modelField(field:(String,String)) = {
    val fName = ScaffoldPlugin.isModelType(field._2) match {
      case true =>  field._1.toLowerCase+"Id"
      case false => field._1
    }
    fName
  }

  def tableField(field:(String, String)) = {
    val columnName = ScaffoldPlugin.isModelType(field._2) match {
      case true =>  field._1.toLowerCase+"_id"
      case false => field._1
    }
    columnName
  }

  def hasReferenceModel(fields:Seq[(String, String)]) = {

    fields.map(f => isModelType(f._2)).contains(true)
  }

  def fieldsWithModel(m:String,fields:Seq[(String,String)]):Seq[(String,String)] = {
    (m,"Required[%s]".format(m.capitalize)) +: fields.filter{ f => isModelType(f._2)}
  }

  def caseClassWithReference(m:String,fields:Seq[(String,String)]) =
    if(hasReferenceModel(fields)){
      fieldsWithModel(m,fields).map(f => extraOptionType(f._2)).mkString("(",",",")")
    } else {
      m.capitalize
    }


}
