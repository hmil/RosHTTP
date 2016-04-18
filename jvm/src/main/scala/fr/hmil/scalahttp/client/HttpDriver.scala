package fr.hmil.scalahttp.client

import java.net.{HttpURLConnection, URL}

import fr.hmil.scalahttp.HttpUtils
import fr.hmil.scalahttp.client.HeaderUtils.CaseInsensitiveString

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

private object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    concurrent.Future {
      try {
        val connection = prepareConnection(req)
        readResponse(connection)
      } catch {
        case e: HttpResponseError => throw e
        case e: Throwable => throw new HttpNetworkError(e)
      }
    }
  }

  private def prepareConnection(req: HttpRequest): HttpURLConnection = {
    val connection = new URL(req.url).openConnection().asInstanceOf[HttpURLConnection]
    req.headers.foreach(t => connection.addRequestProperty(t._1, t._2))

    connection
  }

  private def readResponse(connection: HttpURLConnection): HttpResponse = {
    val code = connection.getResponseCode
    val headers = Iterator.from(0)
      .map(i => (i, connection.getHeaderField(i)))
      .takeWhile(_._2 != null)
      .flatMap({ t =>
        connection.getHeaderFieldKey(t._1) match {
          case null => None
          case key => Some((new CaseInsensitiveString(key), t._2.mkString.trim))
        }
      }).toMap[CaseInsensitiveString, String]
    val charset = HttpUtils.charsetFromContentType(headers.getOrElse("content-type", null))

    if (code < 400) {
      new HttpResponse(
        code,
        Source.fromInputStream(connection.getInputStream)(charset).mkString,
        headers
      )
    } else {
      throw HttpResponseError.badStatus(new HttpResponse(
        code,
        Source.fromInputStream(connection.getErrorStream)(charset).mkString,
        headers
      ))
    }
  }
}
