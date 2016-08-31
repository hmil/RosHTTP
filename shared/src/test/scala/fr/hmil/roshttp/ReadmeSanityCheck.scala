package fr.hmil.roshttp

import utest._

object ReadmeSanityCheck extends TestSuite {
  // Shims libraryDependencies
  class Dep {
    def %%%(s: String): String = "%%%"
    def %%(s: String): String = "%%"
    def %(s: String): String = "%"
  }
  implicit def fromString(s: String): Dep = new Dep()
  var libraryDependencies = Set[Dep]()
  
  // Silence print output
  def println(s: String): Unit = ()
  
  // Test suite
  val tests = this {
    "Readme snippets compile and run successfully" - {
      
      libraryDependencies += "fr.hmil" %%% "roshttp" % "1.1.0"
      
      
      import fr.hmil.roshttp.HttpRequest
      import monifu.concurrent.Implicits.globalScheduler
      
      /* ... */
      
      // Runs consistently on the jvm, in node.js and in the browser!
      val request = HttpRequest("https://schema.org/WebPage")
      
      request.send().map(response => println(response.body))
      
      import fr.hmil.roshttp.exceptions._
      import fr.hmil.roshttp.response.SimpleHttpResponse
      
      HttpRequest("http://hmil.github.io/foobar")
        .send()
        .onFailure {
          // An HttpResponseException always provides a response
          case e:HttpResponseException =>
            "Got a status: ${e.response.statusCode}"
          case SimpleResponseTimeoutException(partialResponse: Some[SimpleHttpResponse]) =>
            s"Body received before timeout: ${partialResponse.get.body}"
        }
      
      HttpRequest()
        .withProtocol(Protocol.HTTP)
        .withHost("localhost")
        .withPort(3000)
        .withPath("/weather")
        .withQueryParameter("city", "London")
        .send() // GET http://localhost:3000/weather?city=London
      
      // Sets the query string such that the target url ends in "?hello%20world"
      request.withQueryString("hello world")
      
      request
        .withQueryParameter("foo", "bar")
        .withQueryArrayParameter("table", Seq("a", "b", "c"))
        .withQueryObjectParameter("map", Seq(
          "d" -> "dval",
          "e" -> "e value"
        ))
        .withQueryParameters(
          "license" -> "MIT",
          "copy" -> "© 2016"
        )
        /* Query is now:
         foo=bar&table=a&table=b&table=c&map[d]=dval&map[e]=e%20value&license=MIT&copy=%C2%A9%202016
        */
      
      // Set the request method to GET, POST, PUT, etc...
      request.withMethod(Method.PUT).send()
      
      request.withHeader("Accept", "text/html")
      
      request.withHeaders(
        "Accept" -> "text/html",
        "Cookie" -> "sessionid=f00ba242cafe"
      )
      
      HttpRequest("long.source.of/data")
        .withBackendConfig(BackendConfig(
          // Uses stream chunks of at most 1024 bytes
          maxChunkSize = 1024
        ))
        .stream()
      
      request.send().map({res =>
        println(res.headers("Set-Cookie"))
      })
      
      import fr.hmil.roshttp.body.Implicits._
      import fr.hmil.roshttp.body.URLEncodedBody
      
      val urlEncodedData = URLEncodedBody(
        "answer" -> "42",
        "platform" -> "jvm"
      )
      request.post(urlEncodedData)
      // or
      request.put(urlEncodedData)
      
      import fr.hmil.roshttp.body.Implicits._
      import fr.hmil.roshttp.body.JSONBody._
      
      val jsonData = JSONObject(
        "answer" -> 42,
        "platform" -> "node"
      )
      request.post(jsonData)
      
      import java.nio.ByteBuffer
      import scala.io.Source
      import fr.hmil.roshttp.body.StreamBody
      
      val bytes = Source.fromFile("test/resources/icon.png")(scala.io.Codec.ISO8859).map(_.toByte)
      val buffer = ByteBuffer.wrap(bytes.toArray)
      request.post(StreamBody(buffer))
      
      import fr.hmil.roshttp.body.Implicits._
      import fr.hmil.roshttp.body.JSONBody._
      import fr.hmil.roshttp.body._
      
      request.post(MultiPartBody(
        // The name part is sent as plain text
        "name" -> PlainTextBody("John"),
        // The skills part is a complex nested structure sent as JSON
        "skills" -> JSONObject(
          "programming" -> JSONObject(
            "C" -> 3,
            "PHP" -> 1,
            "Scala" -> 5
          ),
          "design" -> 2
        ),
        // The picture is sent using a StreamBody
        "picture" -> StreamBody(buffer, "image/jpeg")
      ))
      
      import fr.hmil.roshttp.util.Utils._
      HttpRequest("my.streaming.source/")
        .stream()
        .map({ r =>
          r.body.foreach(buffer => println(getStringFromBuffer(buffer, "UTF-8")))
        })
  
      "Success"
    }
  }
}
