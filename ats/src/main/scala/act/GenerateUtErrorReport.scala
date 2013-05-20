package act

import java.io.File
import scala.xml._
import util.FileUtils

object GenerateUtErrorReport extends App {

  val base = "D:/Workspace/csdm/src"

  val target = "target/surefire-reports"
 
  val reportFilePath = "E:/Temp/utreport.xml" 
    
  generateReport()

  def generateReport() {
    var report = <report></report>
    val (success,failures,errors) = scanBase()
    
    val total = <summary><success>{ success.child.size }</success><failures>{ failures.child.size }</failures><errors>{ errors.child.size }</errors></summary>

    report = addChild(report, total)
    report = addChild(report, failures)
    report = addChild(report, errors)
    report = addChild(report, success)


    FileUtils.save(report, reportFilePath)
    
  }
  
  def scanBase() = {
    val projects = new File(base).listFiles()
    
    var failures = <failures></failures>
    var success = <success></success>
    var errors = <errors></errors>
    
    projects.filter(_.isDirectory()).foreach( p =>{
      println(p.getName())
      scanProject(p.getAbsolutePath()).foreach( tc => {
        tc.child.foreach(node => 
        node match {
          case <failure>{ _ }</failure> => failures = addChild(failures, tc)
          case <error>{ _ }</error> => errors = addChild(errors, tc)          
          case _ =>  
        }
      )

        if(tc.child.isEmpty) {
          success = addChild(success, tc)
        }
      }
    )
    })
    
    (success,failures,errors)
  }
  
  def scanProject(p: String):Array[Node] = {
    val reportsDir = new File(p + File.separator + target)
    if (reportsDir.isDirectory()) {
      reportsDir.listFiles().filter(_.getName().endsWith(".xml")).map(parseReport(_)).flatten
    } else {
      Array.empty
    }
  }

  def parseReport(f: File)  = {
    XML.loadFile(f) \ ("testcase")
  }
 
  private def addChild(n: Node, newChild: Node) = n match {
    case Elem(prefix, label, attribs, scope, child @ _*) =>
      Elem(prefix, label, attribs, scope, child ++ newChild: _*)
    case _ => error("Can only add children to elements!")
  }
    
}