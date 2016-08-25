package fr.hmil.roshttp

import java.io.IOException
import java.nio.ByteBuffer

import fr.hmil.roshttp.node.Modules.{http, https}
import fr.hmil.roshttp.node.buffer.Buffer
import fr.hmil.roshttp.node.http.{IncomingMessage, RequestOptions}
import monifu.reactive.Ack.{Cancel, Continue}
import monifu.reactive.{Observable, Subscriber}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

private object NodeDriver extends DriverTrait {

  def makeRequest[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T], p: Promise[T]): Unit = {
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
    ), (message: IncomingMessage) => {
      if (message.statusCode >= 300 && message.statusCode < 400 && message.headers.contains("location")) {
        makeRequest(req.withURL(message.headers("location")), factory, p)
      } else {
        var subscribers = Set[Subscriber[ByteBuffer]]()

        message.on("data", { (s: js.Dynamic) =>
          val buf = s.asInstanceOf[Buffer]
          // TODO: factor out ugly function
          val byteBuffer = ByteBuffer.allocate(buf.length)
          var i = 0
          while (i < buf.length) {
            byteBuffer.put(buf.readInt8(i).toByte)
            i += 1
          }
          // TODO: check Observable usage
          subscribers.foreach(sub => sub.onNext(byteBuffer).onComplete {
             case Success(Cancel) =>
               subscribers -= sub
             case Success(Continue) =>
               ()
             case Failure(ex) =>
               subscribers -= sub
               sub.onError(ex)
           })
        })

        val headers = message.headers.toMap[String, String]

        message.on("end", { (s: js.Dynamic) =>
          subscribers.foreach(_.onComplete())
          subscribers = Set() // Clear the references for GC
        })

        val bufferStream = new Observable[ByteBuffer] {
          override def onSubscribe(subscriber: Subscriber[ByteBuffer]): Unit = {
            subscribers += subscriber
          }
        }

        val response = factory(
            message.statusCode,
            bufferStream,
            HeaderMap(headers))
          .foreach({ response =>
            if (message.statusCode < 400) {
              p.success(response)
            } else {
              p.failure(HttpResponseError.badStatus(response))
            }
          })
      }
      ()
    })

    nodeRequest.on("error", { (s: js.Dynamic) =>
      p.failure(new HttpNetworkError(new IOException(s.toString)))
      ()
    })

    req.body.foreach({ part =>
      nodeRequest.write(Converters.byteBufferToNodeBuffer(part.content))
    })

    nodeRequest.end()
  }

  def send[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T]): Future[T] = {
    val p: Promise[T] = Promise[T]()

    makeRequest(req, factory, p)

    p.future
  }

}
