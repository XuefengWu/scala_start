package org.example.gen

import java.io.{File, FileWriter}

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */

object Template {


  def gen(model:String)(implicit baseDir:String){


    def genList(tDirPath:String){
      val out:FileWriter = new FileWriter(tDirPath+"/list.scala.html")
      out.write("""
@(%ss:List[%s], %sForm: Form[String])

@import helper._

@main("%s List") {

    <h1>@%ss.size %s(s)</h1>
    <ul>
        @%ss.map { v =>
            <li>
                @v.title
                @form(routes.%ss.delete%s(v.id)){
                    <input type="submit" value="Delete">
                }
            </li>
        }
    </ul>

    <h2>Add a new %s</h2>
    @form(routes.%ss.new%s) {
        @inputText(%sForm("title"))
        <input type="submit" value="Create">
    }
}
      """.format(model,model.capitalize,model,model.capitalize,model,
        model,model,model.capitalize,model.capitalize,model,model.capitalize,model.capitalize,model))
      out.close()
    }
    //check app/view/[model] folder
    val tDirPath = "%s/app/views/%s".format(baseDir,model)
    val tDir = new File(tDirPath)

    if(!tDir.exists())
      tDir.mkdirs()

    genList(tDirPath)
  }
}
