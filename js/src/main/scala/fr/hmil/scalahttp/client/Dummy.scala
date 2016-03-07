package fr.hmil.scalahttp.client

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.concurrent.ExecutionContext.Implicits.global

@js.native
object Math extends js.Object {
  def random(): Double = js.native
}

/** Dummy application entry point for javascript runtime
  *
  * Uses [[DummyShared]] to greet user with the proper context string
  */
@JSExport
object Dummy extends js.JSApp {
  @JSExport
  def main(): Unit = {
    HttpRequest.create
        .withHost("localhost")
        .withPort(8080)
        .withPath("/")
      .send
      .map(res => println(res.body))


  }

  def greet: Unit = {
    DummyShared.greet("JavaScript")
  }

  def random: Double = Math.random()
}
