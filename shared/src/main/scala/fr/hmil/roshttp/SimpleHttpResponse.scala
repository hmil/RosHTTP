package fr.hmil.roshttp

import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

import monifu.reactive.Observable

import monifu.concurrent.Implicits.globalScheduler

import scala.concurrent.{ExecutionContext, Future}
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
      headers: HeaderMap[String])
      (implicit ec: ExecutionContext, config: HttpConfig): Future[SimpleHttpResponse] = {

    val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))

    bodyStream
      .buffer(FiniteDuration(config.bodyCollectTimeout, TimeUnit.SECONDS))
      .map({ seq =>
        // Allocate maximum expected body length
        val buffer = ByteBuffer.allocate(seq.length * config.streamChunkSize)
        val totalBytes = seq.foldLeft(0)({(count, chunk) =>
          buffer.put(chunk)
          count + chunk.limit
        })
        buffer.limit(totalBytes)
        getStringFromBuffer(buffer, charset)
      })
      // TODO: We might not want to depend on monifu's Scheduler
      .asFuture
      .map({ body =>
        new SimpleHttpResponse(statusCode, body.getOrElse(""), headers)
      })
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