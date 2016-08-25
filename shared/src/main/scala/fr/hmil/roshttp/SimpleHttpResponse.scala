package fr.hmil.roshttp

import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

import monifu.reactive.Observable
import monifu.concurrent.Implicits.globalScheduler

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

/**
 * An HTTP response obtained via an [[HttpRequest]]
 */
class SimpleHttpResponse(
    val statusCode: Int,
    val body: String,
    val headers: HeaderMap[String])
  extends HttpResponse

object SimpleHttpResponse extends HttpResponseFactory[SimpleHttpResponse] {
  override def apply(
      statusCode: Int,
      bodyStream: Observable[ByteBuffer],
      headers: HeaderMap[String]): Future[SimpleHttpResponse] = {

    val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))

    bodyStream
      // TODO: configurable timeout
      .buffer(FiniteDuration(10, TimeUnit.SECONDS))
      .map(_
        // TODO: what happens if chunk cuts a multibyte character?
        .map(b => getStringFromBuffer(b, charset))
        .foldLeft("")({ case (l, r) => l + r})
      ).asFuture
      .map(body => new SimpleHttpResponse(statusCode, body.get, headers))
  }

  private def getStringFromBuffer(byteBuffer: ByteBuffer, charset: String): String = {
    if (byteBuffer.hasArray) {
      new String(byteBuffer.array(), 0, byteBuffer.limit, charset)
    } else {
      val tmp = new Array[Byte](byteBuffer.limit)
      byteBuffer.get(tmp)
      new String(tmp, charset)
    }
  }
}