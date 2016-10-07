package fr.hmil.roshttp

import java.net.{URLDecoder, URLEncoder}

private object CrossPlatformUtils {

  def encodeURIComponent(query: String): String = {
    URLEncoder.encode(query, "UTF-8").replace("+", "%20")
  }

  def decodeURIComponent(query: String): String =
    URLDecoder.decode(query, "UTF-8")
}
