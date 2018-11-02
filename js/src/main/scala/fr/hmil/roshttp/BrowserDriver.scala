package fr.hmil.roshttp

import java.nio.ByteBuffer

import fr.hmil.roshttp.exceptions.{HttpException, RequestException, UploadStreamException}
import fr.hmil.roshttp.response.{HttpResponse, HttpResponseFactory, HttpResponseHeader}
import fr.hmil.roshttp.util.HeaderMap
import monix.execution.Ack.Continue
import monix.execution.{Ack, Scheduler}
import monix.reactive.{Observable, Observer}
import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.ErrorEvent

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.scalajs.js.JavaScriptException
import scala.scalajs.js.typedarray.ArrayBuffer
import scala.util.{Failure, Success}

private object BrowserDriver extends DriverTrait {

  override def send[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T])
    (implicit scheduler: Scheduler): Future[T] = {
    val p: Promise[T] = Promise[T]()

    val xhr = new dom.XMLHttpRequest()
    xhr.open(req.method.toString, req.url)
    xhr.withCredentials = req.crossDomainCookies
    xhr.responseType = "arraybuffer"
    req.headers.foreach(t => xhr.setRequestHeader(t._1, t._2))

    xhr.onerror = { (e: ErrorEvent) =>
      p.failure(RequestException(JavaScriptException(e)))
    }

    val bufferQueue = new ByteBufferQueue(req.backendConfig.internalBufferLength)

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
            new HttpResponseHeader(xhr.status, HeaderMap(headers)),
              bufferQueue.observable,
              req.backendConfig)
            .map({response =>
              if (xhr.status >= 400) {
                throw HttpException.badStatus(response)
              } else {
                response
              }
            })
        )
      } else if (xhr.readyState == dom.XMLHttpRequest.DONE) {
        chopChunk().foreach(bufferQueue.push)
        bufferQueue.end()
      }
    }

    def chopChunk(): Seq[ByteBuffer] = {
      val buffer = xhr.response.asInstanceOf[ArrayBuffer]
      val buffers = ByteBufferChopper.chop(
          Converters.arrayBufferToByteBuffer(buffer),
          req.backendConfig.maxChunkSize)
      buffers
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
    var bytes = 0
    val p = Promise[ByteBuffer]()

    bodyStream.subscribe(new Observer[ByteBuffer] {
      override def onError(ex: Throwable): Unit = p.failure(UploadStreamException(ex))

      override def onComplete(): Unit = p.success(recomposeBody(bufferQueue, bytes))

      override def onNext(elem: ByteBuffer): Future[Ack] = {
        bytes += elem.limit
        bufferQueue.enqueue(elem)
        Future.successful(Continue)
      }
    })

    p.future
  }

  private def recomposeBody(seq: mutable.Queue[ByteBuffer], bytes: Int): ByteBuffer = {
    // Allocate maximum expected body length
    val buffer = ByteBuffer.allocate(bytes)
    seq.foreach(chunk => buffer.put(chunk))
    buffer.rewind()
    buffer
  }
}
