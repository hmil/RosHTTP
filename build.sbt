name := "RösHTTP root project"

crossScalaVersions := Seq("2.10.6", "2.11.7")

lazy val root = project.in(file(".")).
aggregate(scalaHttpJS, scalaHttpJVM)

lazy val scalaHttp = crossProject.in(file("."))
  .configureCross(InBrowserTesting.cross)
  .settings(
    name := "roshttp",
    version := "1.1.0",
    scalaVersion := "2.11.8",
    organization := "fr.hmil",
    licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT")),
    homepage := Some(url("http://github.com/hmil/RosHTTP")),

    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
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

    libraryDependencies += "com.lihaoyi" %%% "utest" % "0.4.3",
    libraryDependencies += "io.monix" %%% "monix" % "2.0.0",

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
