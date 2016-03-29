package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.node.Modules.{HttpModule, http}
import fr.hmil.scalahttp.node.http.{IncomingMessage, RequestOptions}
import org.scalajs.dom
import org.scalajs.dom.raw.ErrorEvent
import java.io.IOException

import fr.hmil.scalahttp.HttpUtils
import fr.hmil.scalahttp.node.buffer.Buffer

import scala.concurrent.{Future, Promise}
import scala.scalajs.js

object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    if (HttpModule.isAvailable) {
      sendWithNode(req)
    } else {
      sendWithXHR(req)
    }
  }

  def makeNodeRequest(req: HttpRequest, p: Promise[HttpResponse]): Unit = {
    val nodeRequest = http.request(RequestOptions(
      hostname = req.host,
      port = req.port,
      method = req.method.name,
      path = req.path
    ), (message: IncomingMessage) => {

      val charset = HttpUtils.charsetFromContentType(message.headers.get("content-type").orNull)

      if (message.statusCode >= 300 && message.statusCode < 400 && message.headers.contains("location")) {
        makeNodeRequest(req.withURL(message.headers("location")), p)
      } else {
        var body = ""

        message.on("data", { (s: js.Dynamic) =>
          val buf = s.asInstanceOf[Buffer]
          body += buf.toString(charset)
          ()
        })

        message.on("end", { (s: js.Dynamic) =>
          if (message.statusCode < 400) {
            p.success(new HttpResponse(message.statusCode, body))
          } else {
            p.failure(HttpException.badStatus(new HttpResponse(message.statusCode, body)))
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

  def sendWithNode(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    makeNodeRequest(req, p)

    p.future
  }

  def sendWithXHR(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    val xhr = new dom.XMLHttpRequest()
    xhr.open(req.method.name, req.url)
    xhr.onerror = { (e: ErrorEvent) =>
      p.failure(HttpException.networkError(new IOException(e.message)))
    }
    xhr.onreadystatechange = { (e: dom.Event) =>
      // TODO: create a stream depending on readystate
      if (xhr.readyState == dom.XMLHttpRequest.DONE) {
        if (xhr.status >= 400) {
          p.failure(HttpException.badStatus(new HttpResponse(xhr.status, xhr.responseText)))
        } else {
          p.success(new HttpResponse(xhr.status, xhr.responseText))
        }
      }
      /*
      readystate values:
      0  UNSENT  Client has been created. open() not called yet.
      1  OPENED  open() has been called.
      2  HEADERS_RECEIVED  send() has been called, and headers and status are available.
      3  LOADING  Downloading; responseText holds partial data.
      4  DONE  The operation is complete.
       */
    }
    xhr.send()

    p.future
  }
}
