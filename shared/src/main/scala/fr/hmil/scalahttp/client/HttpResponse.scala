package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.HttpUtils

/**
 * An HTTP response obtained via an [[HttpRequest]]
 */
class HttpResponse(val statusCode: Int, val rawBody: Array[Byte], val headers: HeaderMap[String]) {

  private val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))
  
  lazy val body: String = new String(rawBody, charset)
}