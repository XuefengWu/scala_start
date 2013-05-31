import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "website"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )

  val common = Project("common", file("common"))

  val crawler = Project("crawler", file("crawler")).dependsOn(common)
	
  val news = play.Project(
    appName + "-news", appVersion, path = file("news")
  ).dependsOn(common)
  
  val adminArea = play.Project(
    appName + "-admin", appVersion, path = file("admin")
  ).dependsOn(common)

  val main = play.Project(appName + "-main", appVersion, appDependencies, path = file("website")).settings(
    // Add your own project settings here    
    scalaVersion := "2.10.1"   
  ).dependsOn(common,crawler,news,adminArea)

}
