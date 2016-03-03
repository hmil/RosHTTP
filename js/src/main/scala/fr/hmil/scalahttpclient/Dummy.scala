package fr.hmil.scalahttpclient

import scala.scalajs.js

@js.native
object Math extends js.Object {
  def random(): Double = js.native
}

/** Dummy application entry point for javascript runtime
  *
  * Uses [[DummyShared]] to greet user with the proper context string
  */
object Dummy extends js.JSApp {
  def main(): Unit = {
    greet
    HttpDriver.send(new HttpRequest())
  }

  def greet: Unit = {
    DummyShared.greet("JavaScript")
  }

  def random: Double = Math.random()
}
