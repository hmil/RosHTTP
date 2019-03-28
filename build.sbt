name := "RösHTTP root project"

crossScalaVersions := Seq("2.10.6", "2.11.11", "2.12.2")

lazy val root = project.in(file("."))
  .aggregate(scalaHttpJS, scalaHttpJVM)

lazy val scalaHttp = crossProject.in(file("."))
  .configureCross(InBrowserTesting.cross)
  .settings(
    name := "roshttp",
    version := "2.2.3",
    scalaVersion := "2.11.11",
    organization := "fr.hmil",
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    homepage := Some(url("http://github.com/hmil/RosHTTP")),

    pomExtra := (
      <scm>
        <url>git@github.com:hmil/RosHTTP.git</url>
        <connection>scm:git:git@github.com:hmil/RosHTTP.git</connection>
      </scm>
      <developers>
        <developer>
          <id>hmil</id>
          <name>Hadrien Milano</name>
          <url>https://github.com/hmil/</url>
        </developer>
      </developers>
    ),
    pomIncludeRepository := { _ => false },

    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.4.5" % Test,
    libraryDependencies += "io.monix" %%% "monix" % "2.3.3",

    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .jvmSettings(
    // jvm-specific settings
  )
  .jsSettings(
    // js-specific settings
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1",

    jsEnv := NodeJSEnv().value
  )

lazy val scalaHttpJVM = scalaHttp.jvm
lazy val scalaHttpJS = scalaHttp.js
