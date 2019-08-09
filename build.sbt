name := "RösHTTP root project"

crossScalaVersions := Seq("2.12.9", "2.13.0")

lazy val root = project.in(file("."))
  .aggregate(scalaHttpJS, scalaHttpJVM)

lazy val scalaHttp = crossProject.in(file("."))
  .configureCross(InBrowserTesting.cross)
  .settings(
    name := "roshttp",
    version := "2.2.5",
    scalaVersion := "2.12.9",
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

    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.6.9" % Test,
    libraryDependencies += "io.monix" %%% "monix" % "3.0.0-RC3",

    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .jvmSettings(
    // jvm-specific settings
  )
  .jsSettings(
    // js-specific settings
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7",

    jsEnv := NodeJSEnv().value
  )

lazy val scalaHttpJVM = scalaHttp.jvm
lazy val scalaHttpJS = scalaHttp.js
