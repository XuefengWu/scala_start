sbtPlugin := true

name := "example-plugin"

organization := "org.example"

publishArtifact in (Compile, packageDoc) := false

publishMavenStyle := true

projectID <<= (organization,moduleName,version,artifacts,crossPaths){ (org,module,version,as,crossEnabled) =>
	ModuleID(org, module, version).cross(crossEnabled).artifacts(as : _*)
}

libraryDependencies ++= Seq(  
  "org.specs2" %% "specs2" % "1.13",
  "org.hamcrest" % "hamcrest-all" % "1.1",
  "org.mockito" % "mockito-all" % "1.8.5",
  "junit" % "junit" % "4.7",
  "org.pegdown" % "pegdown" % "1.0.2",
  "commons-io" % "commons-io" % "2.1",
  "org.apache.activemq" % "activemq-core" % "5.4.3",
  "xstream" % "xstream" % "1.2.2",
  "org.antlr" % "stringtemplate" % "4.0.2"
)

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
				"releases"  at "http://scala-tools.org/repo-releases",
				"typesafe repo"  at "http://repo.typesafe.com/typesafe/releases/")