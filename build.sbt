name := "scala-http-client root project"

lazy val root = project.in(file(".")).
  aggregate(scalaHttpJS, scalaHttpJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val scalaHttp = crossProject.in(file(".")).
  settings(
    name := "scala-http-client",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.7",
    organization := "fr.hmil",

    libraryDependencies += "org.scalactic" %%% "scalactic" % "3.0.0-M15",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.0-M15" % "test"
  ).
  jvmSettings(
    // Add JVM-specific settings here
  ).
  jsSettings(
    // Add JS-specific settings here
    scalaJSUseRhino in Global := false
  )

lazy val scalaHttpJVM = scalaHttp.jvm
lazy val scalaHttpJS = scalaHttp.js
