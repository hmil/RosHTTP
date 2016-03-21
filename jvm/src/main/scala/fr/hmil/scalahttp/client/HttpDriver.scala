package fr.hmil.scalahttp.client

import java.io._
import java.net.{HttpURLConnection, URL}
import java.util.concurrent.Executors
import scala.io.Source

import scala.concurrent
import scala.concurrent.{ExecutionContext, Promise, Future}

object HttpDriver {

  implicit val ec = new ExecutionContext {
    val threadPool = Executors.newWorkStealingPool()

    def execute(runnable: Runnable): Unit = {
      threadPool.submit(runnable)
    }

    def reportFailure(t: Throwable): Unit = {}
  }

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
