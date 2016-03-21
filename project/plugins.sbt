addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.7")

resolvers += "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.ivy2/local"

addSbtPlugin("org.scalastyle" % "scalastyle-sbt-plugin" % "0.8.0")
