package fr.hmil.scalahttp.client

import utest._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object HttpRequestSpec extends TestSuite {

  private val serverRequest = HttpRequest.create withHost "localhost" withPort 3000

  /*
   * Status codes defined in HTTP/1.1 spec
   */
  private val goodStatus = List(
    // We do not support 1xx status codes
    200, 201, 202, 203, 204, 205, 206,
    300, 301, 302, 303, 304, 305, 306, 307
  )

  private def badStatus = {
    val base = List(
      400, 401, 402, 403, 404, 405, 406, 408, 409,
      410, 411, 412, 413, 414, 415, 416, 417,
      500, 501, 502, 503, 504, 505
    )
    if (JsEnvUtils.isChrome) {
      // Chrome does not support userspace 407 error handling
      // see: https://bugs.chromium.org/p/chromium/issues/detail?id=372136
      base
    } else {
      407 :: base
    }
  }

  private val statusText = Map(
    200 -> "OK",
    201 -> "Created",
    202 -> "Accepted",
    203 -> "Non-Authoritative Information",
    204 -> "",
    205 -> "",
    206 -> "Partial Content",
    300 -> "Multiple Choices",
    301 -> "Moved Permanently",
    302 -> "Found",
    303 -> "See Other",
    304 -> "",
    305 -> "Use Proxy",
    306 -> "306",
    307 -> "Temporary Redirect",
    400 -> "Bad Request",
    401 -> "Unauthorized",
    402 -> "Payment Required",
    403 -> "Forbidden",
    404 -> "Not Found",
    405 -> "Method Not Allowed",
    406 -> "Not Acceptable",
    407 -> "Proxy Authentication Required",
    408 -> "Request Timeout",
    409 -> "Conflict",
    410 -> "Gone",
    411 -> "Length Required",
    412 -> "Precondition Failed",
    413 -> "Payload Too Large",
    414 -> "URI Too Long",
    415 -> "Unsupported Media Type",
    416 -> "Range Not Satisfiable",
    417 -> "Expectation Failed",
    500 -> "Internal Server Error",
    501 -> "Not Implemented",
    502 -> "Bad Gateway",
    503 -> "Service Unavailable",
    504 -> "Gateway Timeout",
    505 -> "HTTP Version Not Supported"
  )

  val tests = this{
    "The test server should be reachable" - {
      serverRequest
        .withPath("/")
        .send() map { s => assert(s.statusCode == 200) }
    }

    "Status codes < 400 should complete the request with success" - {
      goodStatus.map(status => {
        serverRequest
          .withPath(s"/status/$status")
          .send()
          .map({ s =>
            assert(s.statusCode == status)
          })
      }).reduce((f1, f2) => f1.flatMap(_=>f2))
    }

    "Status codes >= 400 should complete the request with failure" - {
      badStatus.map(status =>
        serverRequest
          .withPath(s"/status/$status")
          .send()
          .failed.map(_ => "success")
      ).reduce((f1, f2) => f1.flatMap(_=>f2))
    }

    "The message body can be obtained on failed requests" - {
      badStatus.map(status =>
        serverRequest
          .withPath(s"/status/$status")
          .send()
          .failed.map {
            case e:HttpException if e.response.isDefined =>
              statusText(e.response.get.statusCode) ==> e.response.get.body
            case _ => assert(false)
          }
      ).reduce((f1, f2) => f1.flatMap(_=>f2))
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
