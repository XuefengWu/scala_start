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
@(%ss:List[%s], %sForm: Form[%s])

@import helper._

@main("%s List") {

    <h1>@%ss.size %s(s)</h1>
<table class="zebra-striped">
    <thead>
    <tr>
        <th>Title</th>
        <th>Action</th>
    </tr>
    </thead>
    <tbody>
        @%ss.map { v =>
        <tr>
         <td><a href="@routes.%ss.edit(v.id.get)">@v.title</a></td>
          <td>@form(routes.%ss.delete(v.id.get)){
                    <input type="submit" value="Delete">
                }
          </td>
        </tr>
        }
    </tbody>
    </table>

    <h2>Add a new %s</h2>
    <a class="btn success" id="add" href="@routes.%ss.create()">Add a new %s</a>
}
      """.format(model,model.capitalize,model,model.capitalize,model.capitalize,
        model, model,model,model.capitalize,model.capitalize,
        model.capitalize,model.capitalize,model))

      out.close()
    }

    def genEdit(tDirPath:String) {
      val out:FileWriter = new FileWriter(tDirPath+"/edit.scala.html")
      out.write("""
@(id: Long, %sForm: Form[%s])

@import helper._

@implicitFieldConstructor = @{ FieldConstructor(twitterBootstrapInput.f) }

@main("edit") {

    <h1>Edit %s</h1>

    @form(routes.%ss.update(id)) {

        <fieldset>
           @inputText(%sForm("title"))
        </fieldset>

        <div class="actions">
            <input type="submit" value="Save this %s" class="btn primary"> or
            <a href="@routes.%ss.list()" class="btn">Cancel</a>
        </div>

    }

    @form(routes.%ss.delete(id), 'class -> "topRight") {
        <input type="submit" value="Delete this %s" class="btn danger">
    }

}
      """.format(model,model.capitalize,
        model,model.capitalize,model,
        model,model.capitalize,model.capitalize,model))
      out.close()
    }
    def genCreate(tDirPath:String) {
      val out:FileWriter = new FileWriter(tDirPath+"/create.scala.html")
      out.write("""
@(%sForm: Form[%s])

@import helper._

@implicitFieldConstructor = @{ FieldConstructor(twitterBootstrapInput.f) }

@main("create") {

    <h1>Add a %s</h1>

    @form(routes.%ss.save()) {

        <fieldset>
            @inputText(%sForm("title"), '_label -> "%s name")
        </fieldset>

        <div class="actions">
            <input type="submit" value="Create this %s" class="btn primary"> or
            <a href="@routes.%ss.list()" class="btn">Cancel</a>
        </div>

    }

}
      """.format(model,model.capitalize,
        model,model.capitalize,model,model,
        model,model.capitalize))
      out.close()
    }
    //check app/view/[model] folder
    val tDirPath = "%s/app/views/%s".format(baseDir,model)
    val tDir = new File(tDirPath)

    if(!tDir.exists())
      tDir.mkdirs()

    genList(tDirPath)
    genEdit(tDirPath)
    genCreate(tDirPath)
  }
}
