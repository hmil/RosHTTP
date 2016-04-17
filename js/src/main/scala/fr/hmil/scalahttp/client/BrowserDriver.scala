package fr.hmil.scalahttp.client

import org.scalajs.dom
import org.scalajs.dom.raw.ErrorEvent

import scala.concurrent.{Future, Promise}
import scala.scalajs.js.JavaScriptException

object BrowserDriver {
  def send(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    val xhr = new dom.XMLHttpRequest()
    xhr.open(req.method.name, req.url)

    req.headers.foreach(t => xhr.setRequestHeader(t._1, t._2))

    xhr.onerror = { (e: ErrorEvent) =>
      p.failure(new HttpNetworkError(new JavaScriptException(e)))
    }
    xhr.onreadystatechange = { (e: dom.Event) =>
      if (xhr.readyState == dom.XMLHttpRequest.DONE) {
        if (xhr.status >= 400) {
          p.failure(HttpResponseError.badStatus(new HttpResponse(xhr.status, xhr.responseText)))
        } else {
          p.success(new HttpResponse(xhr.status, xhr.responseText))
        }
      }
    }
    xhr.send()

    p.future
  }
}
