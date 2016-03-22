package fr.hmil.scalahttp.client

import utest._

import scala.concurrent.ExecutionContext.Implicits.global

object HttpRequestSpec extends TestSuite {

  private val serverRequest = HttpRequest.create withHost "localhost" withPort 3000

  val tests = this{
    "The test server should be reachable" - {
      serverRequest
        .withPath("/")
        .send() map { s => assert(s.statusCode == 200) }
    }
    "Status codes < 400 should complete the request with success" - {
      serverRequest
        .withPath("/status/200")
        .send()
        .map({ s => assert(s.statusCode == 200)})
    }
    "Status codes >= 400 should complete the request with failure" - {
      val req = serverRequest
        .withPath("/status/400")
        .send()

      req.failed.map(_ => "success")
    }
  }
}
/*
class HttpRequestSpec extends AsyncFlatSpec with Matchers {

  private val serverRequest = HttpRequest.create withHost "localhost" withPort 3000


  "The test server" should "be reachable" in {
    serverRequest
      .withPath("/")
      .send() map { s => s.statusCode should be (200) }
  }
  "Status codes < 400" should "complete the request with success" in {
    serverRequest
      .withPath("/status/200")
      .send()
      .map({ s => s.statusCode should be (200)})
  }

  "Status codes >= 400" should "complete the request with failure" in {
    val req = serverRequest
      .withPath("/status/400")
      .send()

    req.recover({case t => "The failure" should be ("have happened")})
      .map(s => "The failure" should be ("have happened"))
  }

}
*/