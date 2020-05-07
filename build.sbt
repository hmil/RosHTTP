name := "RösHTTP root project"

crossScalaVersions := Seq("2.12.11", "2.13.2")

lazy val root = project.in(file("."))
  .aggregate(scalaHttpJS, scalaHttpJVM)

lazy val scalaHttp = crossProject(JSPlatform, JVMPlatform).in(file("."))
 // .configureCross(InBrowserTesting.cross)
  .settings(
    name := "roshttp",
    version := "2.3.0",
    scalaVersion := "2.13.2",
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

    libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.1.6",
    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.7.4" % Test,
    libraryDependencies += "io.monix" %%% "monix" % "3.2.1",

    testFrameworks += new TestFramework("utest.runner.Framework")
  )
  .jvmSettings(
    // jvm-specific settings
  )
  .jsSettings(
    // js-specific settings
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.0.0",

    jsEnv := new org.scalajs.jsenv.nodejs.NodeJSEnv()
  )

lazy val scalaHttpJVM = scalaHttp.jvm
lazy val scalaHttpJS = scalaHttp.js
