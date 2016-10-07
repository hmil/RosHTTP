package fr.hmil.roshttp

import java.io.IOException
import java.nio.ByteBuffer

import fr.hmil.roshttp.ByteBufferQueue.Feeder
import fr.hmil.roshttp.exceptions.{HttpException, RequestException, UploadStreamException}
import fr.hmil.roshttp.node.Modules.{http, https}
import fr.hmil.roshttp.node.buffer.Buffer
import fr.hmil.roshttp.node.http.{IncomingMessage, RequestOptions}
import fr.hmil.roshttp.response.{HttpResponse, HttpResponseFactory, HttpResponseHeader}
import fr.hmil.roshttp.util.HeaderMap
import monix.execution.Ack.Continue
import monix.execution.{Ack, Scheduler}
import monix.reactive.Observer

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.JavaScriptException

private object NodeDriver extends DriverTrait {

  def makeRequest[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T], p: Promise[T])
      (implicit scheduler: Scheduler): Unit = {
    val module = {
      if (req.protocol == Protocol.HTTP)
        http
      else
        https
    }
    val nodeRequest = module.request(RequestOptions(
      hostname = req.host,
      port = req.port.orUndefined,
      method = req.method.toString,
      headers = js.Dictionary(req.headers.toSeq: _*),
      path = req.longPath
    ), handleResponse(req, factory, p)_)
    nodeRequest.on("error", { (s: js.Dynamic) =>
      p.tryFailure(new RequestException(new IOException(s.toString)))
      ()
    })
    if (req.body.isDefined) {
      req.body.foreach({ part =>
        part.content.subscribe(new Observer[ByteBuffer] {
          override def onError(ex: Throwable): Unit = {
            p.tryFailure(new UploadStreamException(ex))
            nodeRequest.abort()
          }

          override def onComplete(): Unit = {
            nodeRequest.end()
          }

          override def onNext(elem: ByteBuffer): Future[Ack] = {
            nodeRequest.write(Converters.byteBufferToNodeBuffer(elem))
            Continue
          }
        })
      })
    } else {
      nodeRequest.end()
    }
  }

  def handleResponse[T <: HttpResponse](req:HttpRequest, factory: HttpResponseFactory[T], p: Promise[T])
        (message: IncomingMessage)(implicit scheduler: Scheduler): Unit = {
    if (message.statusCode >= 300 && message.statusCode < 400 && message.headers.contains("location")) {
      makeRequest(req.withURL(message.headers("location")), factory, p)
    } else {
      val headers = message.headers.toMap[String, String]
      val bufferQueue = new ByteBufferQueue(64,
        new Feeder {
          override def onFlush(): Unit = message.resume()
          override def onFull(): Unit = message.pause()
        })

      message.on("data", { (nodeBuffer: js.Dynamic) =>
        convertAndChopBuffer(nodeBuffer, req.backendConfig.maxChunkSize).foreach(bufferQueue.push)
      })
      message.on("end", { (s: js.Dynamic) =>
        bufferQueue.end()
      })
      message.on("error", { (s: js.Dynamic) =>
        bufferQueue.pushError(JavaScriptException(s))
      })

      p.completeWith(
        factory(new HttpResponseHeader(message.statusCode, HeaderMap(headers)),
          bufferQueue.observable,
          req.backendConfig)
        .map({ response =>
          if (message.statusCode < 400) {
            response
          } else {
            throw HttpException.badStatus(response)
          }
        }))
    }
    ()
  }

  def send[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T])(implicit scheduler: Scheduler):
      Future[T] = {
    val p: Promise[T] = Promise[T]()
    makeRequest(req, factory, p)
    p.future
  }

  private def convertAndChopBuffer(nodeBuffer: js.Any, maxChunkSize: Int): Seq[ByteBuffer] = {
    val buffer = Converters.nodeBufferToByteBuffer(nodeBuffer.asInstanceOf[Buffer])
    ByteBufferChopper.chop(buffer, maxChunkSize)
  }

}
