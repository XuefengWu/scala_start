sbtPlugin := true

name := "example-plugin"

organization := "org.example"

publishArtifact in (Compile, packageDoc) := false

publishMavenStyle := true

projectID <<= (organization,moduleName,version,artifacts,crossPaths){ (org,module,version,as,crossEnabled) =>
	ModuleID(org, module, version).cross(crossEnabled).artifacts(as : _*)
}
