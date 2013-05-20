import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "utreport"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    "com.typesafe.slick" %% "slick" % "1.0.1-RC1",
    "org.slf4j" % "slf4j-nop" % "1.6.6",
    "org.slf4j" % "slf4j-api" % "1.6.6",
    "com.h2database" % "h2" % "1.3.166",
    "org.xerial" % "sqlite-jdbc" % "3.6.20",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.0" % "test",
    "org.seleniumhq.selenium" % "selenium-java" % "2.31.0",
    "com.github.wookietreiber" %% "scala-chart" % "0.2.1",
    "com.typesafe" %% "play-plugins-mailer" % "2.1.0",
    "mysql" % "mysql-connector-java" % "5.1.19"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
