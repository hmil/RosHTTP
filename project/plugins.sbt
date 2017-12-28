val scalaJSVersion = Option(System.getenv("SCALAJS_VERSION")).getOrElse("0.6.19")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)

addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.1")

libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.2.0"

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.3")
