import sbt._
import sbt.Keys._
import org.scalajs.sbtplugin.ScalaJSPluginInternal.scalaJSTestSettings
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import org.scalajs.jsenv.selenium._

object InBrowserTesting {

  lazy val testAll = TaskKey[Unit]("test-all", "Run tests in all test platforms.")

  val ConfigFirefox = config("firefox")
  val ConfigChrome  = config("chrome")

  private def browserConfig(cfg: Configuration, env: SeleniumJSEnv): Project => Project =
    _.settings(
      inConfig(cfg)(Defaults.testSettings ++ scalaJSTestSettings))

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

  def cross: CrossProject => CrossProject =
    _.jvmConfigure(jvm).jsConfigure(js)
}
