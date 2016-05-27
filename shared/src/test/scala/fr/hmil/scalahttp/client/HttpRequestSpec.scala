package fr.hmil.scalahttp.client

import java.nio.ByteBuffer

import fr.hmil.scalahttp.Method.Implicits._
import fr.hmil.scalahttp.Protocol
import fr.hmil.scalahttp.JsEnvUtils
import fr.hmil.scalahttp.body.JSONBody._
import fr.hmil.scalahttp.body.Implicits._
import fr.hmil.scalahttp.body._
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

  private val legalMethods = {
    val base = "GET" :: "POST" :: "HEAD" :: "OPTIONS" :: "PUT" :: "DELETE" :: Nil
    if (JsEnvUtils.isRealBrowser) {
      // Browsers cannot send TRACE requests
      base
    } else {
      "TRACE" :: base
    }
  }

  private val IMAGE_BYTES: Array[Byte] = List[Int](
    0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52, 0x00, 0x00,
    0x00, 0x08, 0x00, 0x00, 0x00, 0x08, 0x08, 0x06, 0x00, 0x00, 0x00, 0xC4, 0x0F, 0xBE, 0x8B, 0x00, 0x00, 0x00,
    0x06, 0x62, 0x4B, 0x47, 0x44, 0x00, 0xFF, 0x00, 0xFF, 0x00, 0xFF, 0xA0, 0xBD, 0xA7, 0x93, 0x00, 0x00, 0x00,
    0x09, 0x70, 0x48, 0x59, 0x73, 0x00, 0x00, 0x0B, 0x13, 0x00, 0x00, 0x0B, 0x13, 0x01, 0x00, 0x9A, 0x9C, 0x18,
    0x00, 0x00, 0x00, 0x07, 0x74, 0x49, 0x4D, 0x45, 0x07, 0xE0, 0x05, 0x0A, 0x0B, 0x1A, 0x39, 0x9E, 0xB0, 0x43,
    0x04, 0x00, 0x00, 0x00, 0xF2, 0x49, 0x44, 0x41, 0x54, 0x18, 0xD3, 0x45, 0xCD, 0xBD, 0x4A, 0xC3, 0x50, 0x1C,
    0x40, 0xF1, 0x93, 0xFB, 0xBF, 0xB9, 0x4D, 0xD2, 0x56, 0xD4, 0x98, 0xA1, 0x14, 0x05, 0x51, 0x50, 0xA8, 0x76,
    0x13, 0x1C, 0x14, 0xF1, 0x19, 0x7C, 0x07, 0x71, 0xE9, 0x03, 0x08, 0x4E, 0xBE, 0x85, 0x83, 0x83, 0x9B, 0x8B,
    0x8F, 0xA0, 0x93, 0x64, 0xB0, 0xA0, 0x45, 0x07, 0x6D, 0xD0, 0xCD, 0x8F, 0x45, 0x84, 0x54, 0xA9, 0xB1, 0xF9,
    0x72, 0x50, 0xE8, 0x99, 0x7F, 0x70, 0x2C, 0xFE, 0xBB, 0xBF, 0xB8, 0x6C, 0x3E, 0x77, 0xF6, 0x9A, 0x55, 0xB7,
    0xB6, 0x5E, 0x29, 0xF3, 0x35, 0x67, 0x94, 0x6E, 0xB4, 0xEE, 0x7A, 0xF3, 0x16, 0xC0, 0xA9, 0x1F, 0xEC, 0x9A,
    0xA2, 0x38, 0xF2, 0x6C, 0x83, 0xA7, 0x2C, 0x6A, 0xA2, 0x09, 0x1C, 0x27, 0x9E, 0x7D, 0x8A, 0x26, 0x35, 0xC0,
    0x57, 0x59, 0x5A, 0x43, 0xC7, 0x61, 0xA0, 0x35, 0x6F, 0x65, 0x41, 0x94, 0x7C, 0x23, 0x9F, 0xB1, 0x02, 0xD0,
    0x00, 0xFE, 0xE2, 0xC2, 0xB5, 0x2B, 0xF6, 0xAD, 0x79, 0x7D, 0x59, 0x6A, 0xA5, 0x59, 0xA5, 0xE3, 0x78, 0x50,
    0xAD, 0xCB, 0xF2, 0x20, 0xFE, 0x03, 0x3F, 0xFD, 0x68, 0xB5, 0xAE, 0xED, 0x76, 0x43, 0x14, 0x13, 0x22, 0xF4,
    0x8B, 0x9C, 0x30, 0x4B, 0x13, 0x00, 0x05, 0xF0, 0x61, 0x2A, 0x61, 0xB7, 0xBD, 0x72, 0x76, 0xEC, 0x4F, 0x0D,
    0x0F, 0xB5, 0xE2, 0x3C, 0x4B, 0xD9, 0x16, 0x71, 0xC7, 0x0B, 0xCA, 0xCD, 0xC6, 0x4D, 0x6F, 0x67, 0xCB, 0x18,
    0x5C, 0x11, 0x8C, 0x36, 0xB8, 0xDA, 0x1E, 0x03, 0x94, 0x4A, 0x64, 0x26, 0x88, 0xF2, 0x74, 0xF4, 0xC0, 0xB4,
    0xFF, 0x2E, 0x62, 0x85, 0x73, 0xDD, 0xAB, 0x93, 0xC7, 0xFD, 0x03, 0x7E, 0x01, 0x01, 0x9A, 0x49, 0xCF, 0xD0,
    0xA6, 0xE4, 0x8F, 0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, 0xAE, 0x42, 0x60, 0x82).map(_.toByte).toArray

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
        HttpRequest("https://schema.org/WebPage")
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
          .withProtocol(Protocol.HTTP)
          .withHost("localhost")
          .withPort(3000)
          .withPath("/query")
          .withQueryParameter("city", "London")
          .send()
      }

      "Query parameters" - {
        HttpRequest()
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

      "User profile form data" - {
        HttpRequest(s"$SERVER_URL/body")
          .post(MultiPartBody(
            "name" -> PlainTextBody("John"),
            "skills" -> JSONObject(
              "programming" -> JSONObject(
                "C" -> 3,
                "PHP" -> 1,
                "Scala" -> 5
              ),
              "design" -> 2
            ),
            "picture" -> StreamBody(ByteBuffer.wrap(IMAGE_BYTES), "image/jpeg")
          ))
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
            .map(r => println(r.headers("X-Status-Code") + " : " + r.statusCode))
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
      "can be set to HTTP and HTTPS" - {
        HttpRequest()
          .withProtocol(Protocol.HTTP)
          .withProtocol(Protocol.HTTPS)
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

    "Http method" - {

      "can be set to any legal value" - {
        legalMethods.map(method =>
          HttpRequest(s"$SERVER_URL/method")
            .withMethod(method)
            .send()
            .map(_.headers("X-Request-Method") ==> method)
        ).reduce((f1, f2) => f1.flatMap(_=>f2))
      }

      "ignores case and capitalizes" - {
        legalMethods.map(method =>
          HttpRequest(s"$SERVER_URL/method")
            .withMethod(method.toLowerCase)
            .send()
            .map(_.headers("X-Request-Method") ==> method)
        ).reduce((f1, f2) => f1.flatMap(_=>f2))
      }

      "cannot be set to an illegal value" - {
        intercept[IllegalArgumentException] {
          HttpRequest(SERVER_URL)
            .withMethod("Wuf")
          assert(false)
        }
      }
    }

    "Request body" - {

      "can be POSTed with ASCII strings" - {
        HttpRequest(s"$SERVER_URL/body")
          .post(PlainTextBody("Hello world"))
          .map({ res =>
            res.body ==> "Hello world"
            res.headers("Content-Type").toLowerCase ==> "text/plain; charset=utf-8"
          })
      }

      "can be POSTed with non-ascii strings" - {
        HttpRequest(s"$SERVER_URL/body")
          .post(PlainTextBody("Heizölrückstoßabdämpfung"))
          .map({ res =>
            res.body ==> "Heizölrückstoßabdämpfung"
            res.headers("Content-Type").toLowerCase ==> "text/plain; charset=utf-8"
          })
      }

      "can be POSTed as multipart" - {
        val part = MultiPartBody(
          "foo" -> PlainTextBody("bar"),
          "engine" -> PlainTextBody("Heizölrückstoßabdämpfung")
        )

        HttpRequest(s"$SERVER_URL/body")
          .post(part)
          .map({ res =>
            res.body ==> "{\"foo\":\"bar\",\"engine\":\"Heizölrückstoßabdämpfung\"}"
            res.headers("Content-Type").toLowerCase ==> s"multipart/form-data; boundary=${part.boundary}; charset=utf-8"
          })
      }

      "can be POSTed as urlencoded" - {
        val part = URLEncodedBody(
          "foo" -> "bar",
          "engine" -> "Heizölrückstoßabdämpfung"
        )

        HttpRequest(s"$SERVER_URL/body")
          .post(part)
          .map({ res =>
            res.body ==> "{\"foo\":\"bar\",\"engine\":\"Heizölrückstoßabdämpfung\"}"
            res.headers("Content-Type").toLowerCase ==> s"application/x-www-form-urlencoded; charset=utf-8"
          })
      }

      "can be POSTed as json" - {
        val part = JSONObject(
          "foo" -> 42,
          "engine" -> "Heizölrückstoßabdämpfung",
          "\"quoted'" -> "Has \" quotes"
        )

        HttpRequest(s"$SERVER_URL/body")
          .post(part)
          .map({ res =>
            res.body ==> "{\"foo\":42,\"engine\":\"Heizölrückstoßabdämpfung\"," +
              "\"\\\"quoted'\":\"Has \\\" quotes\"}"
            res.headers("Content-Type").toLowerCase ==> s"application/json; charset=utf-8"
          })
      }

      "can post a file" - {
        HttpRequest(s"$SERVER_URL/upload/icon.png")
          .post(ByteBuffer.wrap(IMAGE_BYTES))
      }
    }
  }
}
