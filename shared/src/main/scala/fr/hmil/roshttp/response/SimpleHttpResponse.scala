package fr.hmil.roshttp.response

import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

import fr.hmil.roshttp.BackendConfig
import fr.hmil.roshttp.exceptions.SimpleResponseTimeoutException
import fr.hmil.roshttp.util.{HeaderMap, Utils}
import monix.execution.Scheduler
import monix.reactive.Observable

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.util.Success

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

    val charset = Utils.charsetFromContentType(headers.getOrElse("content-type", null))
    val buffers = mutable.Queue[ByteBuffer]()
    val promise = Promise[SimpleHttpResponse]()

    val streamCollector = bodyStream.
      foreach(elem => buffers.enqueue(elem)).
      map({_ =>
        val body = recomposeBody(buffers, config.maxChunkSize, charset)
        new SimpleHttpResponse(statusCode, headers, body)
      })


    val timeoutTask = scheduler.scheduleOnce(config.bodyCollectTimeout, TimeUnit.SECONDS,
      new Runnable {
        override def run(): Unit = {
          val partialBody = recomposeBody(buffers, config.maxChunkSize, charset)
          promise.failure(SimpleResponseTimeoutException(
              Some(new SimpleHttpResponse(statusCode, headers, partialBody))))
          streamCollector.cancel()
        }
      })

    streamCollector.onComplete({
      case res:Success[SimpleHttpResponse] =>
        timeoutTask.cancel()
        promise.trySuccess(res.value)
    })

    promise.future
  }

  private def recomposeBody(seq: mutable.Queue[ByteBuffer], maxChunkSize: Int, charset: String): String = {
    // Allocate maximum expected body length
    val buffer = ByteBuffer.allocate(seq.length * maxChunkSize)
    val totalBytes = seq.foldLeft(0)({ (count, chunk) =>
      buffer.put(chunk)
      count + chunk.limit
    })
    buffer.limit(totalBytes)
    Utils.getStringFromBuffer(buffer, charset)
  }
}