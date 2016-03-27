package fr.hmil.scalahttp.client

import java.net.{HttpURLConnection, URL}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    concurrent.Future {
      val connection = new URL(req.url).openConnection().asInstanceOf[HttpURLConnection]
      val code = connection.getResponseCode

      if (code < 400) {
        new HttpResponse(
          code,
          Source.fromInputStream(connection.getInputStream).mkString
        )
      } else {
        throw HttpException.badStatus(new HttpResponse(
          code,
          Source.fromInputStream(connection.getErrorStream).mkString
        ))
      }
    }
  }
}
