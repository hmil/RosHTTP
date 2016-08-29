package fr.hmil.roshttp

import java.nio.ByteBuffer

import fr.hmil.roshttp.ByteBufferChopper.Finite
import fr.hmil.roshttp.exceptions.{HttpNetworkException, HttpResponseException}
import fr.hmil.roshttp.response.{HttpResponse, HttpResponseFactory}
import fr.hmil.roshttp.util.HeaderMap
import monifu.concurrent.Scheduler
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.ErrorEvent

import scala.concurrent.{Future, Promise}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.JavaScriptException
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

private object BrowserDriver extends DriverTrait {

  override def send[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T])
    (implicit scheduler: Scheduler): Future[T] = {
    val p: Promise[T] = Promise[T]()

    val xhr = new dom.XMLHttpRequest()
    xhr.open(req.method.toString, req.url)
    xhr.responseType = "arraybuffer"
    req.headers.foreach(t => xhr.setRequestHeader(t._1, t._2))

    xhr.onerror = { (e: ErrorEvent) =>
      p.failure(new HttpNetworkException(new JavaScriptException(e)))
    }

    val bufferQueue = new ByteBufferQueue()

    var currentPosition = 0

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
      } else if (xhr.readyState == dom.XMLHttpRequest.LOADING) {
        bufferQueue.push(chopChunk())
      } else if (xhr.readyState == dom.XMLHttpRequest.DONE) {
        bufferQueue.push(chopChunk())
        bufferQueue.end()
      }
    }

    def chopChunk(): Seq[ByteBuffer] = {
      val buffer = xhr.response.asInstanceOf[ArrayBuffer]
      val buffers = ByteBufferChopper.chop(
          new FiniteArrayBuffer(buffer, currentPosition),
          req.backendConfig.maxChunkSize,
          readChunk)
      currentPosition = buffer.byteLength
      buffers
    }

    def readChunk(buffer: FiniteArrayBuffer, start: Int, length: Int): ByteBuffer = {
      TypedArrayBuffer.wrap(buffer.buffer, start + currentPosition, length)
    }

    xhr.send(req.body.map(b => Ajax.InputData.byteBuffer2ajax(b.content)).orUndefined)

    p.future
  }

  private class FiniteArrayBuffer(val buffer: ArrayBuffer, currentPosition: Int) extends Finite {
    override def length: Int = buffer.byteLength - currentPosition
  }
}
