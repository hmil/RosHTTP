package fr.hmil.scalahttp

import java.net.{URLDecoder, URLEncoder}

private object CrossPlatformUtils {

  def oneByteCharset: String = "ISO-8859-1"

  def encodeQueryString(query: String): String = {
    URLEncoder.encode(query, "UTF-8").replace("+", "%20")
  }

  def decodeQueryString(query: String): String =
    URLDecoder.decode(query, "UTF-8")
}
