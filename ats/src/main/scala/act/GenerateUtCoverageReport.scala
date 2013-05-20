package act

import java.net.URL
import scala.io.Source
import scala.xml._

object GenerateUtCoverageReport extends App {

  val base = "http://localhost:9000"
  val coverageApi = "/api/resources?resource=com.carestream.csdm:csdm-parent&depth=-1&metrics=ncloc,coverage&format=xml"
    
  parseCoverage()
  
  def parseCoverage() = {
    val url = new URL(base+coverageApi)
    val projects = XML.load(url) \ ("resource")
    
    projects.foreach(p => {
        val k = p \ ("key") text
        
        println(k)
        
        (p \ ("msr")).foreach(n => {
          
          println(n \ ("key") text)
          println(n \ ("frmt_val") text)
        })
    }
    )
    
  }
}