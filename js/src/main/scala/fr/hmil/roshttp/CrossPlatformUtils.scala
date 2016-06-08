package fr.hmil.roshttp

import scala.scalajs.js


private object CrossPlatformUtils {

  def oneByteCharset: String =  {
    if (JsEnvUtils.isRealBrowser)
      "ISO-8859-1"
    else
      "binary"
  }

  def encodeQueryString(query: String): String =
    js.URIUtils.encodeURIComponent(query)

  def decodeQueryString(query: String): String =
    js.URIUtils.decodeURIComponent(query)
}
