package fr.hmil.scalahttp.client

import java.io.IOException

import org.scalajs.dom
import org.scalajs.dom.raw.ErrorEvent

import scala.concurrent.{Future, Promise}

object BrowserDriver {
  def send(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    val xhr = new dom.XMLHttpRequest()
    xhr.open(req.method.name, req.url)
    xhr.onerror = { (e: ErrorEvent) =>
      p.failure(HttpException.networkError(new IOException(e.message)))
    }
    xhr.onreadystatechange = { (e: dom.Event) =>
      if (xhr.readyState == dom.XMLHttpRequest.DONE) {
        if (xhr.status >= 400) {
          p.failure(HttpException.badStatus(new HttpResponse(xhr.status, xhr.responseText)))
        } else {
          p.success(new HttpResponse(xhr.status, xhr.responseText))
        }
      }
    }
    xhr.send()

    p.future
  }
}
