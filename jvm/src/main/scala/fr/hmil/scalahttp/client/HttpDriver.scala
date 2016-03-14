package fr.hmil.scalahttp.client

import java.io._
import java.net.{HttpURLConnection, URL}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

import scala.concurrent
import scala.concurrent.{Promise, Future}

object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    println("Connecting to " + req.url)
    val connection = new URL(req.url).openConnection().asInstanceOf[HttpURLConnection]

    concurrent.Future {
      println("fuu")

      // Doesn't work:
      val res = new HttpResponse(
        connection.getResponseCode,
        new BufferedReader(new InputStreamReader(connection.getInputStream)).readLine()
      )
      // Works:
      // val res = new HttpResponse(
      //   42,
      //   "test"
      // )
      println("bar")
      res
    }
  }
}
