package act

import java.io.File
import org.apache.commons.io.FileUtils
import java.util.Date
object GenerateFileLastUpdateReport extends App {

  val dirPath = "C:/ProgramData/TW/PAS/log"
    
  val  dir = new File(dirPath)
  
  val reporter = new File("d:/%s.rep".format(dir.getName()))
  
  reporter.delete()
  
  dir.listFiles().foreach{ f =>
    FileUtils.writeStringToFile(reporter,"%s\t%s\n".format(f.lastModified(),new Date(f.lastModified())),true)
  }
  
  dir.listFiles().groupBy(f => new Date(f.lastModified()).getDay() * 24 * 60 +  new Date(f.lastModified()).getHours() * 60 + new Date(f.lastModified()).getMinutes()).foreach{ f =>
    println(f._1 + "\t" + f._2.size + "\t" + new Date(f._1 * 60 * 1000))
  }
  
}