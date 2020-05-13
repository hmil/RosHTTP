import sbt._
import sbt.Keys._
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.jsdependencies.sbtplugin.JSDependenciesPlugin.autoImport._

import org.scalajs.jsenv.selenium._

object InBrowserTesting {

  lazy val testAll = TaskKey[Unit]("test-all", "Run tests in all test platforms.")

  val ConfigFirefox = config("firefox")
  val ConfigChrome  = config("chrome")

  private def browserConfig(cfg: Configuration, env: SeleniumJSEnv): Project => Project =
    _.settings(
      inConfig(cfg)(
        Defaults.testSettings ++
      //  scalaJSTestSettings ++
        Seq(

          // Scala.JS public settings
          scalaJSLinkerConfig           := (scalaJSLinkerConfig           in Test).value,
          fastOptJS                     := (fastOptJS                     in Test).value,
          fullOptJS                     := (fullOptJS                     in Test).value,
          jsDependencies                := (jsDependencies                in Test).value,
          jsDependencyFilter            := (jsDependencyFilter            in Test).value,
          jsDependencyManifest          := (jsDependencyManifest          in Test).value,
          jsDependencyManifests         := (jsDependencyManifests         in Test).value,
          jsManifestFilter              := (jsManifestFilter              in Test).value,
       // loadedJSEnv                   := (loadedJSEnv                   in Test).value,
          packageJSDependencies         := (packageJSDependencies         in Test).value,
          packageMinifiedJSDependencies := (packageMinifiedJSDependencies in Test).value,
          resolvedJSDependencies        := (resolvedJSDependencies        in Test).value,
       // resolvedJSEnv                 := (resolvedJSEnv                 in Test).value,
       // scalaJSConsole                := (scalaJSConsole                in Test).value,
          scalaJSIR                     := (scalaJSIR                     in Test).value,
          scalaJSLinkedFile             := (scalaJSLinkedFile             in Test).value,
          scalaJSNativeLibraries        := (scalaJSNativeLibraries        in Test).value,
          scalajsp                      := (scalajsp                      in Test).evaluated,
          scalaJSStage                  := (scalaJSStage                  in Test).value,

          // Scala.JS internal settings
          scalaJSLinker          := (scalaJSLinker          in Test).value,

          // SBT test settings
          definedTestNames     := (definedTestNames     in Test).value,
          definedTests         := (definedTests         in Test).value,
       // executeTests         := (executeTests         in Test).value,
       // loadedTestFrameworks := (loadedTestFrameworks in Test).value,
       // testExecution        := (testExecution        in Test).value,
       // testFilter           := (testFilter           in Test).value,
          testForkedParallel   := (testForkedParallel   in Test).value,
       // testFrameworks       := (testFrameworks       in Test).value,
          testGrouping         := (testGrouping         in Test).value,
       // testListeners        := (testListeners        in Test).value,
       // testLoader           := (testLoader           in Test).value,
       // testOnly             := (testOnly             in Test).value,
          testOptions          := (testOptions          in Test).value,
       // testQuick            := (testQuick            in Test).value,
          testResultLogger     := (testResultLogger     in Test).value,
       // test                 := (test                 in Test).value,

          // In-browser settings
          jsEnv           := env)))

  def js: Project => Project = {
    //val materializer = new CustomFileMaterializer("test/server/runtime", "http://localhost:3000/runtime")
    _.configure(
      browserConfig(ConfigFirefox, new SeleniumJSEnv(org.openqa.selenium.remote.DesiredCapabilities.firefox())),
      browserConfig(ConfigChrome, new SeleniumJSEnv(org.openqa.selenium.remote.DesiredCapabilities.chrome())))
      .settings(
        testAll := {
          (test in Test).value
          (test in ConfigFirefox).value
          (test in ConfigChrome).value
        })
  }

  def jvm: Project => Project =
    _.settings(
      testAll := (test in Test).value)

}
