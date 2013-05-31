import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "scala123"
  val appVersion      = "1.0-SNAPSHOT"  

  scalaVersion := "2.10.1" 



  val common = Project("common", file("common")).settings(
    // Add your own project settings here    
    scalaVersion := "2.10.1" 
  )

  val crawler = Project("crawler", file("crawler")).dependsOn(common)
	
  val news = play.Project(
    appName + "-news", appVersion, path = file("news")
  ).dependsOn(common)
  
  val adminArea = play.Project(
    appName + "-admin", appVersion, path = file("admin")
  ).dependsOn(common)

  val mainDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )

  val main = play.Project(appName + "-main", appVersion, appDependencies, path = file("website")).settings(
    // Add your own project settings here    

  ).dependsOn(common,crawler,news,adminArea)

}
