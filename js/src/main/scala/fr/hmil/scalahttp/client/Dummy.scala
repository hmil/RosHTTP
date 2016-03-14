package fr.hmil.scalahttp.client

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
    DummyShared.main()
  }
}
