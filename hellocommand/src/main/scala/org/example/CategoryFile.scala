/**
 * Created by IntelliJ IDEA.
 * User: Orna
 * Date: 12-3-25
 * Time: 下午4:05
 * To change this template use File | Settings | File Templates.
 */

import java.io.File

val dir = "E:\\TDDOWNLOAD\\VoidBook\\家常读书"
val Cat = "(.*)【家常读书[·|.](.*)】(.*)".r
new File(dir).listFiles().foreach{f =>
  println(f.getName)
  f.getName match {
    case Cat(n,v,l) => {
      println(v)
      val subDir = new File(dir + "\\" + v)
      if(!subDir.exists()) {
          subDir.mkdirs()
      }
      f.renameTo(new File(dir + "\\" + v + "\\" + f.getName))
    }
    case _ =>
  }
}

def copy(src:File, dest:File) {
  import java.io.{File,FileInputStream,FileOutputStream}
  new FileOutputStream(dest) getChannel() transferFrom(
    new FileInputStream(src) getChannel, 0, Long.MaxValue )
}
