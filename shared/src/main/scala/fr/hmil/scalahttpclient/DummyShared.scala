package fr.hmil.scalahttpclient

/** Shared utilities for dummy scripts */
object DummyShared {

  /** Greets users with additional information about the message's origin
    *
    * @param context The execution context from which the greeting message is being sent
    */
  def greet(context: String): Unit = println("Hello world from " + context + "!")

  def nativeRandom: Double = Dummy.random
}
