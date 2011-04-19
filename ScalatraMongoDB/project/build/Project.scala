import sbt._
class build(info: ProjectInfo) extends DefaultWebProject(info) {
// scalatra
val scalatraVersion = "2.0.0-SNAPSHOT"
val jettyGroupId = "org.eclipse.jetty"
val jettyVersion = "7.3.1.v20110307"
  
val sonatypeNexusSnapshots = "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
val sonatypeNexusReleases = "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"
val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
 
// jetty
val jetty7 = jettyGroupId % "jetty-webapp" % jettyVersion % "test"
val jetty7websocket = jettyGroupId % "jetty-websocket" % jettyVersion % "compile" 
val jettytester = jettyGroupId % "test-jetty-servlet" % jettyVersion % "compile"
val servletApi = "javax.servlet" % "servlet-api" % "2.5" % "provided"
 
//casbah
val casbah = "com.mongodb.casbah" %% "casbah" % "2.0.1"

//Socket.IO
val websocket = jettyGroupId % "jetty-websocket" % jettyVersion % "provided"
val socketio = "com.glines.socketio" %% "socketio"
val socketiocore = "com.glines.socketio" %% "socketio-core"
val scalatraSocketIO = "org.scalatra" %% "scalatra-socketio" % scalatraVersion
val socketIoJava = "org.scalatra.socketio-java" % "socketio" % "2.0.0-SNAPSHOT"
val socketIoJavaCore = "org.scalatra.socketio-java" % "socketio-core" % "2.0.0-SNAPSHOT"

//scalate
val scalatraScalate = "org.scalatra" %% "scalatra-scalate" % scalatraVersion 
val slf4jBinding = "ch.qos.logback" % "logback-classic" % "0.9.25" % "runtime"

}