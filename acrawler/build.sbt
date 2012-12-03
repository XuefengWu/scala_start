name := "ACrawler"

version := "1.0"

scalaVersion := "2.10.0-RC3"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies +=
  "com.typesafe.akka" %% "akka-actor" % "2.1.0-RC3" cross CrossVersion.full
  
libraryDependencies += "com.ning" % "async-http-client" % "1.7.8"

libraryDependencies += "org.apache.commons" % "commons-io" % "1.3.2"
                          
libraryDependencies ++= List(
  "com.typesafe" % "slick_2.10.0-RC3" % "0.11.2", // use the right version here
  "org.slf4j" % "slf4j-nop" % "1.6.4",
   "mysql" % "mysql-connector-java" % "5.1.21"
) 

libraryDependencies += "org.mongodb" % "mongo-java-driver" % "2.10.0"
                       
libraryDependencies += "commons-httpclient" % "commons-httpclient" % "3.1"
            
                                   