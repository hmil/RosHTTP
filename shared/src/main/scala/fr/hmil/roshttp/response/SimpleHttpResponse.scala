package fr.hmil.roshttp.response

import java.nio.ByteBuffer
import java.util.concurrent.TimeUnit

import fr.hmil.roshttp.BackendConfig
import fr.hmil.roshttp.exceptions.{ResponseException, ResponseTimeoutException}
import fr.hmil.roshttp.util.{HeaderMap, Utils}
import monix.execution.Scheduler
import monix.reactive.Observable

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

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
      header: HttpResponseHeader,
      bodyStream: Observable[ByteBuffer],
      config: BackendConfig)
      (implicit scheduler: Scheduler): Future[SimpleHttpResponse] = {

    val charset = Utils.charsetFromContentType(header.headers.getOrElse("content-type", null))
    val buffers = mutable.Queue[ByteBuffer]()
    val promise = Promise[SimpleHttpResponse]()

    val streamCollector = bodyStream.
      foreach(elem => buffers.enqueue(elem)).
      map({_ =>
        val body = recomposeBody(buffers, config.maxChunkSize, charset)
        new SimpleHttpResponse(header.statusCode, header.headers, body)
      })


    val timeoutTask = scheduler.scheduleOnce(config.bodyCollectTimeout, TimeUnit.SECONDS,
      new Runnable {
        override def run(): Unit = {
          promise.failure(new ResponseTimeoutException(header))
          streamCollector.cancel()
        }
      })

    streamCollector.onComplete({
      case res:Success[SimpleHttpResponse] =>
        timeoutTask.cancel()
        promise.trySuccess(res.value)
      case e:Failure[_] =>
        timeoutTask.cancel()
        promise.tryFailure(new ResponseException(e.exception, header))
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