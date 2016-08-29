package fr.hmil.roshttp.response

import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

import fr.hmil.roshttp.BackendConfig
import fr.hmil.roshttp.exceptions.HttpTimeoutException
import fr.hmil.roshttp.util.{HeaderMap, HttpUtils}
import monifu.concurrent.Scheduler
import monifu.reactive.Ack.{Cancel, Continue}
import monifu.reactive.{Observable, Observer}

import scala.collection.mutable
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}

/**
 * An HTTP response obtained via an [[fr.hmil.roshttp.HttpRequest]]
 */
class SimpleHttpResponse(
    val statusCode: Int,
    val headers: HeaderMap[String],
    val body: String)
  extends HttpResponse

object SimpleHttpResponse extends HttpResponseFactory[SimpleHttpResponse] {
  override def apply(
      statusCode: Int,
      headers: HeaderMap[String],
      bodyStream: Observable[ByteBuffer],
      config: BackendConfig)
      (implicit scheduler: Scheduler): Future[SimpleHttpResponse] = {

    val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))

    val promise = Promise[mutable.Queue[ByteBuffer]]()
    val buffers = mutable.Queue[ByteBuffer]()
    var cancelled = false

    val timeoutTask = scheduler.scheduleOnce(FiniteDuration(config.bodyCollectTimeout, TimeUnit.SECONDS),
      new Runnable {
        override def run(): Unit = {
          val partialBody = recomposeBody(buffers, config.maxChunkSize, charset)
          promise.failure(new HttpTimeoutException(Some(new SimpleHttpResponse(statusCode, headers, partialBody))))
          cancelled = true
        }
      })

    bodyStream.onSubscribe(new Observer[ByteBuffer] {
      def onNext(elem: ByteBuffer) = {
        if (!cancelled) {
          buffers.enqueue(elem)
          Continue
        } else {
          Cancel
        }
      }
      def onComplete() = {
        if (timeoutTask.cancel()) {
          promise.trySuccess(buffers)
        }
      }
      def onError(ex: Throwable) = {
        if (timeoutTask.cancel()) {
          promise.tryFailure(ex)
        }
      }
    })

    promise.future.map({ chunks =>
      val body = recomposeBody(chunks, config.maxChunkSize, charset)
      new SimpleHttpResponse(statusCode, headers, body)
    })
  }

  private def recomposeBody(seq: mutable.Queue[ByteBuffer], maxChunkSize: Int, charset: String): String = {
    // Allocate maximum expected body length
    val buffer = ByteBuffer.allocate(seq.length * maxChunkSize)
    val totalBytes = seq.foldLeft(0)({ (count, chunk) =>
      buffer.put(chunk)
      count + chunk.limit
    })
    buffer.limit(totalBytes)
    getStringFromBuffer(buffer, charset)
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