package fr.hmil.scalahttp.client

import java.nio.ByteBuffer

import fr.hmil.scalahttp.{Converters, HttpUtils}
import fr.hmil.scalahttp.body.BodyPart
import org.scalajs.dom
import org.scalajs.dom.Blob
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.ErrorEvent

import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.JavaScriptException
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer, Uint8Array}

private object BrowserDriver {

  def send(req: HttpRequest, body: Option[BodyPart]): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    val xhr = new dom.XMLHttpRequest()
    xhr.open(req.method.toString, req.url)
    xhr.responseType = "arraybuffer"
    req.headers.foreach(t => xhr.setRequestHeader(t._1, t._2))

    xhr.onerror = { (e: ErrorEvent) =>
      p.failure(new HttpNetworkError(new JavaScriptException(e)))
    }
    xhr.onreadystatechange = { (e: dom.Event) =>
      if (xhr.readyState == dom.XMLHttpRequest.DONE) {
        val headers = xhr.getAllResponseHeaders() match {
          case null => Map[String, String]()
          case s: String => s.split("\r\n").map({ s =>
            val split = s.split(": ")
            (split.head, split.tail.mkString.trim)
          }).toMap[String, String]
        }
        val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))
        val response = new HttpResponse(
          xhr.status,
          TypedArrayBuffer.wrap(xhr.response.asInstanceOf[ArrayBuffer]),
          HeaderMap(headers))
        if (xhr.status >= 400) {
          p.failure(HttpResponseError.badStatus(response))
        } else {
          p.success(response)
        }
      }
    }

    xhr.send(body.map(b => Ajax.InputData.byteBuffer2ajax(b.content)).orUndefined)

    p.future
  }
}
