package fr.hmil.scalahttp.client

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/** Shared utilities for dummy scripts */
object DummyShared {

  def main(): Unit = {
    HttpRequest.create
      .withHost("localhost")
      .withPort(8080)
      .withPath("/")
      .send()
      .onComplete({
        case Success(res) => println(res.body)
        case Failure(t) => println("An error has occurred: " + t.getMessage)
      })
  }
}
