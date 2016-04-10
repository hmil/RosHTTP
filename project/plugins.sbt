addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.7")

addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "0.8.0")

// Currently using dependency on own forked version.
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.1.2-SNAPSHOT"
