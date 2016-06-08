package fr.hmil.roshttp

/*
  Taken from http://japgolly.blogspot.fr/2016/03/scalajs-firefox-chrome-sbt.html
 */

import scala.scalajs.js.Dynamic.global
import scala.util.Try

private object JsEnvUtils {

  /** Sample (real) values are:
    * - Mozilla/5.0 (Unknown; Linux x86_64) AppleWebKit/538.1 (KHTML, like Gecko) PhantomJS/2.1.1 Safari/538.1
    * - Mozilla/5.0 (X11; Linux x86_64; rv:45.0) Gecko/20100101 Firefox/45.0
    * - Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.75 Safari/537.36
    */
  val userAgent: String =
    Try(global.navigator.userAgent.asInstanceOf[String]) getOrElse "Unknown"

  // Check each browser
  val isFirefox = userAgent contains "Firefox"
  val isChrome  = userAgent contains "Chrome"

  val isRealBrowser = isFirefox || isChrome

  // Or you can even just check if running in X
  val isRunningInX = userAgent contains "X11"
}
