name := "ats"

version := "1.0"

scalaVersion := "2.10.0-RC2"

parallelExecution := false

testOptions in Test += Tests.Argument("junitxml")

libraryDependencies ++= Seq(  
  "org.scala-lang" % "scala-reflect" % "2.10.0-RC2",
  "org.scala-lang" % "scala-actors" % "2.10.0-RC2",
  "org.specs2" % "specs2_2.10.0-RC2" % "1.12.2" withSources(),
  "org.hamcrest" % "hamcrest-all" % "1.1",
  "org.mockito" % "mockito-all" % "1.8.5",
  "junit" % "junit" % "4.7",
  "org.pegdown" % "pegdown" % "1.0.2",
  "commons-io" % "commons-io" % "2.1",
  "org.apache.activemq" % "activemq-core" % "5.4.3",
  "xstream" % "xstream" % "1.2.2",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "2.0.0.201206130900-r",
  "com.typesafe.akka" %% "akka-actor" % "2.1.0-RC2" cross CrossVersion.full
)

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
				"releases"  at "http://scala-tools.org/repo-releases",
				"typesafe repo"  at "http://repo.typesafe.com/typesafe/releases/")	