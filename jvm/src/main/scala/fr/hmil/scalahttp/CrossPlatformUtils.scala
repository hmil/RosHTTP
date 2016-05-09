package fr.hmil.scalahttp

import java.net.{URLDecoder, URLEncoder}

/**
  * Created by hadrien on 10.05.16.
  */
private object CrossPlatformUtils {
  def encodeQueryString(query: String): String = {
    URLEncoder.encode(query, "UTF-8").replace("+", "%20")
  }

  def decodeQueryString(query: String): String =
    URLDecoder.decode(query, "UTF-8")
}
