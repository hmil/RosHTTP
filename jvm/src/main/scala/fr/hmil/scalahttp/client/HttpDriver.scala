package fr.hmil.scalahttp.client

import java.net.{HttpURLConnection, URL}

import fr.hmil.scalahttp.HttpUtils

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
    val charset = HttpUtils.charsetFromContentType(connection.getHeaderField("content-type"))

    if (code < 400) {
      new HttpResponse(
        code,
        Source.fromInputStream(connection.getInputStream)(charset).mkString
      )
    } else {
      throw HttpResponseError.badStatus(new HttpResponse(
        code,
        Source.fromInputStream(connection.getErrorStream)(charset).mkString
      ))
    }
  }
}
