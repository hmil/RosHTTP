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
class HttpResponse(val statusCode: Int, val bodyStream: Observable[ByteBuffer], val headers: HeaderMap[String]) {

  private val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))
  
  lazy val body: Future[String] = {
    bodyStream
      // TODO: configurable timeout
      .buffer(FiniteDuration(10, TimeUnit.SECONDS))
      .map(_
        // TODO: what happens if chunk cuts a multibyte character?
        .map(b => getStringFromBuffer(b))
        .foldLeft("")({ case (l, r) => l + r})
      ).asFuture
      .map(_.get)
  }

  private def getStringFromBuffer(byteBuffer: ByteBuffer): String = {
    if (byteBuffer.hasArray) {
      new String(byteBuffer.array(), 0, byteBuffer.limit, charset)
    } else {
      val tmp = new Array[Byte](byteBuffer.limit)
      byteBuffer.get(tmp)
      new String(tmp, charset)
    }
  }


}