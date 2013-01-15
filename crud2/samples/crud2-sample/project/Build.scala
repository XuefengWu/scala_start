import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "crud2-sample"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "crud2" %% "crud2" % "0.1.0.3"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      

  )

}
