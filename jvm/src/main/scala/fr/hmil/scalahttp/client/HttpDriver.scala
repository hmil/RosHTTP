package fr.hmil.scalahttp.client

import java.net.{HttpURLConnection, URL}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    println("Connecting to " + req.url)
    val connection = new URL(req.url).openConnection().asInstanceOf[HttpURLConnection]

    concurrent.Future {
      val res = new HttpResponse(
        connection.getResponseCode(),
        Source.fromInputStream(connection.getInputStream()).mkString
      )
      res
    }
  }
}
