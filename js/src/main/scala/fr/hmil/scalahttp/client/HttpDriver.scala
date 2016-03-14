package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.node.Modules.{HttpModule, http}
import fr.hmil.scalahttp.node.http.{IncomingMessage, RequestOptions}
import org.scalajs.dom
import org.scalajs.dom.raw.ErrorEvent

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.util.Try

object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    if (HttpModule.isAvailable) {
      sendWithNode(req)
    } else {
      sendWithXHR(req)
    }
  }

  def sendWithNode(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    val nodeRequest = http.request(RequestOptions(
      hostname = req.host,
      port = req.port,
      method = req.method.name,
      path = req.path
    ), (message: IncomingMessage) => {

      var body = ""

      message.on("data", { (s: js.Dynamic) =>
        body = body + s
        ()
      })

      message.on("end", { (s: js.Dynamic) =>
        p.success(new HttpResponse(200, body))
        ()
      })

      ()
    })

    nodeRequest.end()

    p.future
  }

  def sendWithXHR(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    val xhr = new dom.XMLHttpRequest()
    xhr.open(req.method.name, req.url)
    xhr.onerror = { (e: ErrorEvent) =>
      // p.failure(new HttpError) TODO
    }
    xhr.onreadystatechange = { (e: dom.Event) =>
       // TODO: create a stream depending on readystate
      if (xhr.readyState == dom.XMLHttpRequest.DONE) p.success(new HttpResponse(xhr.status, xhr.responseText))
      /*
      readystate values:
      0	UNSENT	Client has been created. open() not called yet.
      1	OPENED	open() has been called.
      2	HEADERS_RECEIVED	send() has been called, and headers and status are available.
      3	LOADING	Downloading; responseText holds partial data.
      4	DONE	The operation is complete.
       */
    }
    xhr.send()

    p.future
  }
}
