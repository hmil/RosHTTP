package fr.hmil.scalahttp.client

import java.nio.ByteBuffer

import fr.hmil.scalahttp.HttpUtils

/**
 * An HTTP response obtained via an [[HttpRequest]]
 */
class HttpResponse(val statusCode: Int, val rawBody: ByteBuffer, val headers: HeaderMap[String]) {

  private val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))
  
  lazy val body: String = {
    if (rawBody.hasArray) {
      new String(rawBody.array(), charset)
    } else {
      val tmp = new Array[Byte](rawBody.capacity())
      rawBody.get(tmp)
      new String(tmp, charset)
    }

  }
}