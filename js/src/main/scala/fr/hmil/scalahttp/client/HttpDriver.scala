package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.node.Modules.{HttpModule, http}
import fr.hmil.scalahttp.node.http.{IncomingMessage, RequestOptions}
import org.scalactic.Fail
import org.scalajs.dom
import org.scalajs.dom.raw.ErrorEvent

import scala.concurrent.{Future, Promise}
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
      p.success(new HttpResponse(200, "OK"))
      ()
    })

    nodeRequest.end()

    p.future
  }

  def sendWithXHR(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    val xhr = new dom.XMLHttpRequest()
    xhr.open(req.method.name, req.url)
    xhr.onload = { (e: dom.Event) =>
      p.success(new HttpResponse(xhr.status, xhr.responseText))
    }
    xhr.onerror = { (e: ErrorEvent) =>
      // p.failure(new HttpError) TODO
    }
    xhr.send()

    p.future
  }
}
