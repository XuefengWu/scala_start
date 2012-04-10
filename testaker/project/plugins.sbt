// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0")

libraryDependencies += "org.antlr" % "stringtemplate" % "4.0.2"

libraryDependencies += "org.example" %% "example-plugin" % "0.1-SNAPSHOT"