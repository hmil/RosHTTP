package fr.hmil.scalahttp.client

import java.io.IOException

import fr.hmil.scalahttp.body.BodyPart
import fr.hmil.scalahttp.{HttpUtils, Protocol}
import fr.hmil.scalahttp.node.Modules.{http, https}
import fr.hmil.scalahttp.node.buffer.Buffer
import fr.hmil.scalahttp.node.http.{IncomingMessage, RequestOptions}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

private object NodeDriver {

  def makeRequest(req: HttpRequest, body: Option[BodyPart], p: Promise[HttpResponse]): Unit = {
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
        makeRequest(req.withURL(message.headers("location")), body, p)
      } else {
        var body = ""

        message.on("data", { (s: js.Dynamic) =>
          val buf = s.asInstanceOf[Buffer]
          body += buf.toString(charset)
          ()
        })

        message.on("end", { (s: js.Dynamic) =>
          val headers = message.headers.toMap[String, String]

          val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))
          val response = new HttpResponse(
            message.statusCode,
            body.getBytes(charset),
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

    val charset = HttpUtils.charsetFromContentType(req.headers.getOrElse("content-type", null))

    body.foreach({ part =>
      nodeRequest.write(new String(part.content, charset))
    })

    nodeRequest.end()
  }

  def send(req: HttpRequest, body: Option[BodyPart]): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    makeRequest(req, body, p)

    p.future
  }

}
