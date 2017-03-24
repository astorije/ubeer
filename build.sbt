name := "ubeer"
version := "0.0.0"

description := "TODO"

scalaVersion := "2.12.1"
scalacOptions ++= Seq("-unchecked", "-deprecation","-feature")

libraryDependencies += "org.sangria-graphql" %% "sangria" % "1.0.0"
libraryDependencies += "org.sangria-graphql" %% "sangria-spray-json" % "1.0.0"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.1"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.1"
