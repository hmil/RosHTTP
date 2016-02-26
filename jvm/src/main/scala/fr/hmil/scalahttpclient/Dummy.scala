package fr.hmil.scalahttpclient

import java.lang.Math

/** Dummy application entry point for JVM runtime
  *
  * Uses [[DummyShared]] to greet user with the proper context string
  */
object Dummy {
  def main(args: Array[String]): Unit = {
    DummyShared.greet("JVM")
  }

  def random = Math.random()
}
