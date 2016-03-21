package fr.hmil.scalahttp.client

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers._
import org.scalactic._
import org.scalatest.time.{Seconds, Millis, Span}

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


class DummySpec extends AsyncFlatSpec with Matchers {
  "a" should equal ("a")

/*
  "The Dummy random function" should "return a random number no matter the environment" in {
    Dummy.random should (be >= (0.0) and be < (1.0))
  }

  "The shared Dummy object" should "proxy random to native types" in {
    DummyShared.nativeRandom should (be >= (0.0) and be < (1.0))
  }
*/

  "An HTTP request" should "end in a success" in {
    HttpRequest.create
      .withHost("localhost")
      .withPort(3000)
      .withPath("/")
      .send() map { s => s.statusCode should be (200) }
  }

}
