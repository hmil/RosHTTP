name := "scala-http-client root project"


lazy val root = project.in(file(".")).
aggregate(scalaHttpJS, scalaHttpJVM).
settings(
    publish := {},
    publishLocal := {}
)

lazy val scalaHttp = crossProject.in(file("."))
  .configure(InBrowserTesting.cross)
  .settings(
    name := "scala-http-client",
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.7",
    organization := "fr.hmil",

    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.4.3",

    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .jvmSettings(
    // jvm-specific settings
  )
  .jsSettings(
    // js-specific settings
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.0",

    jsEnv := NodeJSEnv().value
  )

lazy val scalaHttpJVM = scalaHttp.jvm
lazy val scalaHttpJS = scalaHttp.js
