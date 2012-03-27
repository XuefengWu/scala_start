package org.example.gen

import java.io.FileWriter

/**
 * Created by IntelliJ IDEA.
 * User: 19002850
 * Date: 12-3-16
 * Time: 下午4:27
 * To change this template use File | Settings | File Templates.
 */

object Routers {
  def gen(model: String)(implicit baseDir:String) {

    def genRoutes = """

# %ss
GET     /%ss                      controllers.%ss.list
GET     /%ss/new                  controllers.%ss.create
GET     /%ss/:id                  controllers.%ss.edit(id:Long)
POST    /%ss/:id                  controllers.%ss.update(id:Long)
POST    /%ss                      controllers.%ss.save
POST    /%ss/:id/delete           controllers.%ss.delete(id:Long)
    """.format(model.capitalize,
      model,model.capitalize,
      model,model.capitalize,
      model,model.capitalize,
      model,model.capitalize,
      model,model.capitalize,
      model,model.capitalize)

    val out = new FileWriter(baseDir+"/conf/routes",true)
    out.write(genRoutes)
    out.close()
  }
}
