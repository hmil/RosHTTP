package fr.hmil.scalahttp.client

import java.net.{URLDecoder, URLEncoder}

private object CrossPlatformUtils {
  def encodeQueryString(query: String): String = {
    URLEncoder.encode(query, "UTF-8").replace("+", "%20")
  }

  def decodeQueryString(query: String): String =
    URLDecoder.decode(query, "UTF-8")
}
