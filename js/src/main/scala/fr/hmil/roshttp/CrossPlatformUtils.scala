package fr.hmil.roshttp

import scala.scalajs.js


private object CrossPlatformUtils {

  def encodeURIComponent(query: String): String =
    js.URIUtils.encodeURIComponent(query)

  def decodeURIComponent(query: String): String =
    js.URIUtils.decodeURIComponent(query)
}
