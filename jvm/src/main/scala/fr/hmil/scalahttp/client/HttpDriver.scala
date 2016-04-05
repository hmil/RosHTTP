package fr.hmil.scalahttp.client

import java.net.{HttpURLConnection, URL}

import fr.hmil.scalahttp.HttpUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

private object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    concurrent.Future {
      val connection = new URL(req.url).openConnection().asInstanceOf[HttpURLConnection]
      val code = connection.getResponseCode
      val charset = HttpUtils.charsetFromContentType(connection.getHeaderField("content-type"))

      if (code < 400) {
        new HttpResponse(
          code,
          Source.fromInputStream(connection.getInputStream)(charset).mkString
        )
      } else {
        throw HttpException.badStatus(new HttpResponse(
          code,
          Source.fromInputStream(connection.getErrorStream)(charset).mkString
        ))
      }
    }
  }
}
