addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.7")

addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "0.8.0")

// Currently using dependency on own forked version.
// TODO: use official repo once custom-protocol feature has been merged
resolvers += Resolver.url("hmil.fr ivy Repository", new java.net.URL("http://hmil.fr/public/ivy"))(Resolver.ivyStylePatterns)
libraryDependencies += "org.scala-js" %% "scalajs-env-selenium" % "0.1.2-SNAPSHOT_custom-protocol"
