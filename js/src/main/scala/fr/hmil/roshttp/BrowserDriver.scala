package fr.hmil.roshttp

import java.nio.ByteBuffer

import fr.hmil.roshttp.ByteBufferChopper.Finite
import fr.hmil.roshttp.exceptions.{HttpNetworkException, HttpResponseException}
import fr.hmil.roshttp.response.{HttpResponse, HttpResponseFactory}
import fr.hmil.roshttp.util.HeaderMap
import monifu.concurrent.Scheduler
import monifu.reactive.Ack.Continue
import monifu.reactive.{Observable, Observer}
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.ErrorEvent

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.JavaScriptException
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}
import scala.util.{Failure, Success}

private object BrowserDriver extends DriverTrait {

  override def send[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T])
    (implicit scheduler: Scheduler): Future[T] = {
    val p: Promise[T] = Promise[T]()

    val xhr = new dom.XMLHttpRequest()
    xhr.open(req.method.toString, req.url)
    xhr.responseType = "arraybuffer"
    req.headers.foreach(t => xhr.setRequestHeader(t._1, t._2))

    xhr.onerror = { (e: ErrorEvent) =>
      p.failure(new HttpNetworkException(JavaScriptException(e)))
    }

    val bufferQueue = new ByteBufferQueue()

    xhr.onreadystatechange = { (e: dom.Event) =>
      if (xhr.readyState == dom.XMLHttpRequest.HEADERS_RECEIVED) {
        val headers = xhr.getAllResponseHeaders() match {
          case null => Map[String, String]()
          case s: String => s.split("\r\n").map({ s =>
            val split = s.split(": ")
            (split.head, split.tail.mkString.trim)
          }).toMap[String, String]
        }

        p.completeWith(
          factory(
              xhr.status,
              HeaderMap(headers),
              bufferQueue.observable,
              req.backendConfig)
            .map({response =>
              if (xhr.status >= 400) {
                throw HttpResponseException.badStatus(response)
              } else {
                response
              }
            })
        )
      } else if (xhr.readyState == dom.XMLHttpRequest.DONE) {
        bufferQueue.push(chopChunk())
        bufferQueue.end()
      }
    }

    def chopChunk(): Seq[ByteBuffer] = {
      val buffer = xhr.response.asInstanceOf[ArrayBuffer]
      val buffers = ByteBufferChopper.chop(
          new FiniteArrayBuffer(buffer),
          req.backendConfig.maxChunkSize,
          readChunk)
      buffers
    }

    def readChunk(buffer: FiniteArrayBuffer, start: Int, length: Int): ByteBuffer = {
      TypedArrayBuffer.wrap(buffer.buffer, start, length)
    }

    if (req.body.isEmpty) {
      xhr.send()
    } else {
      bufferBody(req.body.get.content).andThen({
        case buffer: Success[ByteBuffer] => xhr.send(Ajax.InputData.byteBuffer2ajax(buffer.value))
        case f: Failure[ByteBuffer] => p.failure(f.exception)
      })
    }

    p.future
  }

  private def bufferBody(bodyStream: Observable[ByteBuffer])(implicit scheduler: Scheduler): Future[ByteBuffer] = {
    val bufferQueue = mutable.Queue[ByteBuffer]()
    val promise = Promise[mutable.Queue[ByteBuffer]]()
    var bytes = 0

    bodyStream.onSubscribe(new Observer[ByteBuffer] {
      def onNext(elem: ByteBuffer) = {
        bytes += elem.limit
        bufferQueue.enqueue(elem)
        Continue
      }

      def onComplete() = {
        promise.trySuccess(bufferQueue)
      }

      def onError(ex: Throwable) = {
        promise.tryFailure(ex)
      }
    })

    promise.future.map(chunks => recomposeBody(chunks, bytes))
  }

  // TODO: factor that with SimpleHttpResponse code
  private def recomposeBody(seq: mutable.Queue[ByteBuffer], bytes: Int): ByteBuffer = {
    // Allocate maximum expected body length
    val buffer = ByteBuffer.allocate(bytes)
    seq.foreach(chunk => buffer.put(chunk))
    buffer.rewind()
    buffer
  }

  private class FiniteArrayBuffer(val buffer: ArrayBuffer) extends Finite {
    override def length: Int = buffer.byteLength
  }
}
