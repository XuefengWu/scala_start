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
    val out = new FileWriter(baseDir+"/conf/routes",true)
    out.write("""

# %ss
GET     /%ss                      controllers.%ss.%ss
POST    /%ss                      controllers.%ss.new%s
POST    /%ss/:id/delete           controllers.%ss.delete%s(id:Long)
    """.format(model.capitalize,model,model.capitalize,model,model,model.capitalize,
      model.capitalize,model,model.capitalize,model.capitalize))

    out.close()
  }
}