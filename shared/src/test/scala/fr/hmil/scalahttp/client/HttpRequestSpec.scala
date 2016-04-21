package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.Protocol
import utest._

import scala.concurrent.ExecutionContext.Implicits.global

object HttpRequestSpec extends TestSuite {

  private val SERVER_URL = "http://localhost:3000"

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

    "Meta" - {
      "The test server should be reachable" - {
        HttpRequest(SERVER_URL)
          .send() map { s => s.statusCode ==> 200 }
      }
    }

    "Examples from the readme actually work" - {
      "Main example" - {
        // (But override println to avoid flooding the console)
        def println(s: String) = assert(s.length > 1000)
        HttpRequest("http://schema.org/WebPage")
          .send()
          .map(response => println(response.body))
      }

      "Error handling" - {
        HttpRequest("http://hmil.github.io/foobar")
          .send()
          .onFailure {
            case e:HttpResponseError =>
              s"Got a status: ${e.response.statusCode}" ==> "Got a status: 404"
          }
      }

      "Composite URI" - {
        HttpRequest()
          .withProtocol("HTTP")
          .withHost("localhost")
          .withPort(3000)
          .withPath("/query")
          .withQueryParameter("city", "London")
          .send()
      }

      "Query parameters" - {
        val q = HttpRequest()
          .withQueryParameter("foo", "bar")
          .withQueryParameter("table", List("a", "b", "c"))
          .withQueryParameter("map", Map(
            "d" -> "dval",
            "e" -> "e value"
          ))
          .withQueryParameters(Map(
            "license" -> "MIT",
            "copy" -> "© 2016"
          ))
          .queryString.get ==>
          "foo=bar&table=a&table=b&table=c&map%5Bd%5D=dval&map%5Be%5D=e%20value&license=MIT&copy=%C2%A9%202016"
      }
    }

    "General" - {
      "Status codes < 400 should complete the request with success" - {
        goodStatus.map(status => {
          HttpRequest(SERVER_URL)
            .withPath(s"/status/$status")
            .send()
            .map({ s =>
              s.statusCode ==> status
            })
        }).reduce((f1, f2) => f1.flatMap(_ => f2))
      }

      "Status codes >= 400 should complete the request with failure" - {
        badStatus.map(status =>
          HttpRequest(SERVER_URL)
            .withPath(s"/status/$status")
            .send()
            .failed.map(_ => "success")
        ).reduce((f1, f2) => f1.flatMap(_ => f2))
      }

      "Redirects are followed" - {
        HttpRequest(SERVER_URL)
          .withPath("/redirect/temporary/echo/redirected")
          .send()
          .map(res => {
            res.body ==> "redirected"
          })
      }
    }

    "Error handling" - {
      "The message body can be obtained on failed requests" - {
        badStatus.map(status =>
          HttpRequest(SERVER_URL)
            .withPath(s"/status/$status")
            .send()
            .failed.map {
            case e:HttpResponseError =>
              statusText(e.response.statusCode) ==> e.response.body
            case _ => assert(false)
          }
        ).reduce((f1, f2) => f1.flatMap(_=>f2))
      }
    }

    "Query string" - {
      "set in constructor" - {
        "vanilla" - {
          HttpRequest(s"$SERVER_URL/query?Hello%20world.")
            .send()
            .map(res => {
              res.body ==> "Hello world."
            })
        }

        "with illegal characters" - {
          HttpRequest(s"$SERVER_URL/query?Heizölrückstoßabdämpfung%20+")
            .send()
            .map(res => {
              res.body ==> "Heizölrückstoßabdämpfung +"
            })
        }
      }

      "set in withQueryString" - {

        "vanilla" - {
          HttpRequest(s"$SERVER_URL/query")
            .withQueryString("Hello world.")
            .send()
            .map(res => {
              res.body ==> "Hello world."
            })
        }

        "with illegal characters" - {
          HttpRequest(s"$SERVER_URL/query")
            .withQueryString("Heizölrückstoßabdämpfung %20+")
            .send()
            .map(res => {
              res.body ==> "Heizölrückstoßabdämpfung %20+"
            })
        }

        "is escaped" - {
          HttpRequest(s"$SERVER_URL/query")
            .withQueryString("Heizölrückstoßabdämpfung")
            .queryString.get ==> "Heiz%C3%B6lr%C3%BCcksto%C3%9Fabd%C3%A4mpfung"
        }
      }

      "set in withRawQueryString" - {
        HttpRequest(s"$SERVER_URL/query")
          .withQueryStringRaw("Heiz%C3%B6lr%C3%BCcksto%C3%9Fabd%C3%A4mpfung")
          .send()
          .map(res => {
            res.body ==> "Heizölrückstoßabdämpfung"
          })
      }

      "set in withQueryParameter" - {
        "single" - {
          HttpRequest(s"$SERVER_URL/query/parsed")
            .withQueryParameter("device", "neon")
            .send()
            .map(res => {
              res.body ==> "{\"device\":\"neon\"}"
            })
        }

        "added in batch" - {
          HttpRequest(s"$SERVER_URL/query/parsed")
            .withQueryParameters(Map(
              "device" -> "neon",
              "element" -> "argon"))
            .send()
            .map(res => {
              res.body ==> "{\"device\":\"neon\",\"element\":\"argon\"}"
            })
        }

        "added in batch with illegal characters" - {
          HttpRequest(s"$SERVER_URL/query/parsed")
            .withQueryParameters(Map(
              " zařízení" -> "topný olej vůle potlačující",
              "chäřac+=r&" -> "+Heizölrückstoßabdämpfung=r&"))
            .send()
            .map(res => {
              res.body ==> "{\" zařízení\":\"topný olej vůle potlačující\"," +
                "\"chäřac+=r&\":\"+Heizölrückstoßabdämpfung=r&\"}"
            })
        }

        "added in sequence" - {
          HttpRequest(s"$SERVER_URL/query/parsed")
            .withQueryParameters(Map(
              "element" -> "argon",
              "device" -> "chair"
            ))
            .withQueryParameter("tool", "hammer")
            .withQueryParameter("device", "neon")
            .send()
            .map(res => {
              res.body ==> "{\"element\":\"argon\",\"device\":[\"chair\",\"neon\"],\"tool\":\"hammer\"}"
            })
        }

        "as list parameter" - {
          HttpRequest(s"$SERVER_URL/query/parsed")
            .withQueryParameter("map", List("foo", "bar"))
            .send()
            .map(res => {
              res.body ==> "{\"map\":[\"foo\",\"bar\"]}"
            })
        }
      }

      "removed" - {
        val req = HttpRequest(s"$SERVER_URL/query/parsed")
          .withQueryString("device=chair")
          .withoutQueryString()

        assert(req.queryString.isEmpty)
      }

    }

    "Protocol" - {
      "can be set to HTTP" - {
        HttpRequest()
          .withProtocol("http")
          .withProtocol("HTTP")
          .withProtocol(Protocol.HTTP)
      }

      "cannot be set to HTTPS" - {
        intercept[IllegalArgumentException] {
          HttpRequest()
            .withProtocol("https")
          assert(false)
        }
      }
    }

    "Request headers" - {
      "Can be set with a map" - {
        val headers = Map(
          "accept" -> "text/html, application/xhtml",
          "Cache-Control" -> "max-age=0",
          "custom" -> "foobar")

        val req = HttpRequest(s"$SERVER_URL/headers")
          .withHeaders(headers)

        // Test with corrected case
        req.headers ==> headers

        req.send().map(res => {
          assert(res.body.contains("\"accept\":\"text/html, application/xhtml\""))
          assert(res.body.contains("\"cache-control\":\"max-age=0\""))
          assert(res.body.contains("\"custom\":\"foobar\""))
        })
      }

      "Can be set individually" - {
        val req = HttpRequest(s"$SERVER_URL/headers")
          .withHeader("cache-control", "max-age=0")
          .withHeader("Custom", "foobar")

        req.headers ==> Map(
            "cache-control" -> "max-age=0",
            "Custom" -> "foobar")

        req.send().map(res => {
          assert(res.body.contains("\"cache-control\":\"max-age=0\""))
          assert(res.body.contains("\"custom\":\"foobar\""))
        })
      }

      "Overwrite previous value when set" - {
        val req = HttpRequest(s"$SERVER_URL/headers")
          .withHeaders(Map(
            "accept" -> "text/html, application/xhtml",
            "Cache-Control" -> "max-age=0",
            "custom" -> "foobar"
          ))
          .withHeaders(Map(
            "Custom" -> "barbar",
            "Accept" -> "application/json"
          ))
          .withHeader("cache-control", "max-age=128")

        req.headers ==> Map(
          "cache-control" -> "max-age=128",
          "Custom" -> "barbar",
          "Accept" -> "application/json")

        req.send().map(res => {
          assert(res.body.contains("\"cache-control\":\"max-age=128\""))
          assert(res.body.contains("\"custom\":\"barbar\""))
          assert(res.body.contains("\"accept\":\"application/json\""))
        })
      }
    }

    "Response headers" - {

      "can be read in the general case" - {
        HttpRequest(s"$SERVER_URL/")
          .send()
          .map({
            res =>
              println(res.headers)
              res.headers("X-Powered-By") ==> "Express"
          })
      }

      "can be read in the error case" - {
        HttpRequest(s"$SERVER_URL/status/400")
          .send()
          .failed.map {
            case e: HttpResponseError =>
              e.response.headers("X-Powered-By") ==> "Express"
          }
      }
    }
  }
}
