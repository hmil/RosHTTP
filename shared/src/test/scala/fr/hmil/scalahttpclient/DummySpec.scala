package fr.hmil.scalahttpclient

import org.scalatest._
import org.scalatest.matchers._
import org.scalactic._


class DummySpec extends FlatSpec with Matchers {
  "a" should (equal ("a"))

  "The Dummy random function" should "return a random number no matter the environment" in {
    Dummy.random should (be >= (0.0) and be < (1.0))
  }

  "The shared Dummy object" should "proxy random to native types" in {
    DummyShared.nativeRandom should (be >= (0.0) and be < (1.0))
  }
}
