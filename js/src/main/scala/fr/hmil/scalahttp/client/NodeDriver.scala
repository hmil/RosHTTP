package fr.hmil.scalahttp.client

import java.io.IOException
import java.nio.ByteBuffer

import fr.hmil.scalahttp.{Converters, HttpUtils, Protocol}
import fr.hmil.scalahttp.node.Modules.{http, https}
import fr.hmil.scalahttp.node.buffer.Buffer
import fr.hmil.scalahttp.node.http.{IncomingMessage, RequestOptions}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

private object NodeDriver extends AbstractDriver{

  // Accumulates chunks received by the request and turns them into a ByteBuffer
  private class BufferAccumulator {
    private var acc = List[Array[Byte]]()

    def append(buf: Buffer): Unit = {
      val length = buf.length
      var i = 0
      val chunk = new Array[Byte](length)
      while(i < length) {
        chunk(i) = buf.readInt8(i).toByte
        i += 1
      }
      acc ::= chunk
    }

    def collect(): ByteBuffer = {
      val length = acc.foldRight(0)((chunk, l) => l + chunk.length)
      val buffer = ByteBuffer.allocate(length)
      acc.foreach(chunk => {
        var i = 0
        while (i < chunk.length) {
          buffer.put(chunk(i))
          i += 1
        }
      })
      buffer
    }
  }

  def makeRequest(req: HttpRequest, p: Promise[HttpResponse]): Unit = {
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

      val charset = HttpUtils.charsetFromContentType(message.headers.get("content-type").orNull)

      if (message.statusCode >= 300 && message.statusCode < 400 && message.headers.contains("location")) {
        makeRequest(req.withURL(message.headers("location")), p)
      } else {
        val body = new BufferAccumulator()

        message.on("data", { (s: js.Dynamic) =>
          val buf = s.asInstanceOf[Buffer]
          body.append(buf)
          ()
        })

        message.on("end", { (s: js.Dynamic) =>
          val headers = message.headers.toMap[String, String]

          val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))
          val response = new HttpResponse(
            message.statusCode,
            body.collect(),
            HeaderMap(headers))

          if (message.statusCode < 400) {
            p.success(response)
          } else {
            p.failure(HttpResponseError.badStatus(response))
          }
          ()
        })
      }
      ()
    })

    nodeRequest.on("error", { (s: js.Dynamic) =>
      p.failure(new IOException(s.toString))
      ()
    })

    req.body.foreach({ part =>
      nodeRequest.write(Converters.byteBufferToNodeBuffer(part.content))
    })

    nodeRequest.end()
  }

  def send(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    makeRequest(req, p)

    p.future
  }

}
