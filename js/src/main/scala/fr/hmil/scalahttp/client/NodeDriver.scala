package fr.hmil.scalahttp.client

import java.io.IOException

import fr.hmil.scalahttp.HttpUtils
import fr.hmil.scalahttp.client.HeaderUtils.CaseInsensitiveString
import fr.hmil.scalahttp.node.Modules._
import fr.hmil.scalahttp.node.buffer.Buffer
import fr.hmil.scalahttp.node.http.{IncomingMessage, RequestOptions}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js

object NodeDriver {

  def makeRequest(req: HttpRequest, p: Promise[HttpResponse]): Unit = {
    val nodeRequest = http.request(RequestOptions(
      hostname = req.host,
      port = req.port,
      method = req.method.name,
      headers = js.Dictionary(req.headers.toSeq: _*),
      path = req.longPath
    ), (message: IncomingMessage) => {

      val charset = HttpUtils.charsetFromContentType(message.headers.get("content-type").orNull)

      if (message.statusCode >= 300 && message.statusCode < 400 && message.headers.contains("location")) {
        makeRequest(req.withURL(message.headers("location")), p)
      } else {
        var body = ""

        message.on("data", { (s: js.Dynamic) =>
          val buf = s.asInstanceOf[Buffer]
          body += buf.toString(charset)
          ()
        })

        message.on("end", { (s: js.Dynamic) =>
          val response = new HttpResponse(
            message.statusCode, body,
            message.headers
              .map({ t => (new CaseInsensitiveString(t._1), t._2)})
              .toMap[CaseInsensitiveString, String])
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

    nodeRequest.end()
  }

  def send(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    makeRequest(req, p)

    p.future
  }

}
