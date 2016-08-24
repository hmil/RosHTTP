package fr.hmil.roshttp

import scala.scalajs.js


private object CrossPlatformUtils {

  def encodeQueryString(query: String): String =
    js.URIUtils.encodeURIComponent(query)

  def decodeQueryString(query: String): String =
    js.URIUtils.decodeURIComponent(query)
}
