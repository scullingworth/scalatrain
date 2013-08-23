organization := "com.typesafe.training"

name := "scalatrain"

version := "0.1.0"

scalaVersion := "2.10.2"

libraryDependencies ++= Dependencies.scalatrain

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-language:_",
  "-target:jvm-1.6",
  "-encoding", "UTF-8"
)

// initialCommands in console := "import com.typesafe.training.scalatrain._"
