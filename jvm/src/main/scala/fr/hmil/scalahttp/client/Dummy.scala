package fr.hmil.scalahttp.client

import java.lang.Math

/** Dummy application entry point for JVM runtime
  *
  * Uses [[DummyShared]] to greet user with the proper context string
  */
object Dummy {
  def main(args: Array[String]): Unit = {
    greet
  }

  def greet: Unit = {
    DummyShared.greet("Java")
  }

  def random: Double = Math.random()
}
