# RösHTTP
[![Build Status](https://travis-ci.org/hmil/RosHTTP.svg?branch=master)](https://travis-ci.org/hmil/RosHTTP)
[![Latest release](https://hmil.github.io/RosHTTP/version-badge.svg)](https://github.com/hmil/RosHTTP)
[![Scala.js](https://www.scala-js.org/assets/badges/scalajs-0.6.8.svg)](https://www.scala-js.org)

A human-readable scala http client API compatible with:

- vanilla jvm **scala**
- most **browsers** (_via_ [scala-js](https://github.com/scala-js/scala-js))
- **node.js** (_via_ [scala-js](https://github.com/scala-js/scala-js))

# Installation

Add a dependency in your build.sbt:

```scala
libraryDependencies += "fr.hmil" %%% "roshttp" % "1.1.0"
```

# Usage

The following is a simplified usage guide. You may find useful information in
the [API doc](http://hmil.github.io/RosHTTP/docs/index.html) too.
## Basic usage

```scala
import fr.hmil.roshttp.HttpRequest
import monix.execution.Scheduler.Implicits.global
import scala.util.{Failure, Success}
import fr.hmil.roshttp.response.SimpleHttpResponse

// Runs consistently on the jvm, in node.js and in the browser!
val request = HttpRequest("https://schema.org/WebPage")

request.send().onComplete({
    case res:Success[SimpleHttpResponse] => println(res.get.body)
    case e: Failure[SimpleHttpResponse] => e.exception.printStackTrace()
  })
```

When you `send()` a request, you get a `Future` which resolves to
a SimpleHttpResponse if everything went fine or fails with a HttpNetworkException if a
network error occurred or with a HttpResponseException if a statusCode >= 400 was received.
When applicable, the response body of a failed request can be read:

```scala
import fr.hmil.roshttp.exceptions._

HttpRequest("http://hmil.github.io/foobar")
  .send()
  .onFailure {
    // An HttpResponseException always provides a response
    case e:HttpResponseException =>
      "Got a status: ${e.response.statusCode}"
    case SimpleResponseTimeoutException(partialResponse: Some[SimpleHttpResponse]) =>
      s"Body received before timeout: ${partialResponse.get.body}"
  }
```


## Configuring requests

[HttpRequests](http://hmil.github.io/RosHTTP/docs/index.html#fr.hmil.roshttp.HttpRequest)
are immutable objects. They expose methods named `.withXXX` which can be used to
create more complex requests.

### URI

The URI can be passed as argument of the request constructor or `.withURI`.
The URI can be built using `.withProtocol`, `.withHost`, `.withPort`,
`.withPath`, and `.withQuery...`. The latter is a bit more complex and
is detailed below.

```scala
import fr.hmil.roshttp.Protocol.HTTP

HttpRequest()
  .withProtocol(HTTP)
  .withHost("localhost")
  .withPort(3000)
  .withPath("/weather")
  .withQueryParameter("city", "London")
  .send() // GET http://localhost:3000/weather?city=London
```

#### `.withQueryString`
The whole querystring can be set to a custom value like this:

```scala
// Sets the query string such that the target url ends in "?hello%20world"
request.withQueryString("hello world")
```

`.withQueryString(string)` urlencodes string and replaces the whole query string
with the result.
To bypass encoding, use `.withQueryStringRaw(rawString)`.

#### `.withQueryParameter`
Most of the time, the query string is used to pass key/value pairs in the
`application/x-www-form-urlencoded` format.
[HttpRequest](http://hmil.github.io/RosHTTP/docs/index.html#fr.hmil.roshttp.HttpRequest)
offers an API to add, update and delete keys in the query string.  

```scala
request
  .withQueryParameter("foo", "bar")
  .withQuerySeqParameter("table", Seq("a", "b", "c"))
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
```

### HTTP Method

```scala
import fr.hmil.roshttp.Method.PUT

request.withMethod(PUT).send()
```

### Headers

Set individual headers using `.withHeader`
```scala
request.withHeader("Accept", "text/html")
```
Or multiple headers at once using `.withHeaders`
```scala
request.withHeaders(
  "Accept" -> "text/html",
  "Cookie" -> "sessionid=f00ba242cafe"
)
```

### Backend configuration

Some low-level configuration settings are available in [BackendConfig](http://hmil.github.io/RosHTTP/docs/index.html#fr.hmil.roshttp.BackendConfig).
Each request can use a specific backend configuration using `.withBackendConfig`.

example:
```scala
import fr.hmil.roshttp.BackendConfig

HttpRequest("long.source.of/data")
  .withBackendConfig(BackendConfig(
    // Uses stream chunks of at most 1024 bytes
    maxChunkSize = 1024
  ))
  .stream()
```

## Response headers

A map of response headers is available on the `HttpResponse` object:
```scala
request.send().map({res =>
  println(res.headers("Set-Cookie"))
})
```

## Sending data

An HTTP request can send data wrapped in an implementation of `BodyPart`. The most common
formats are already provided but you can create your own as well.   
A set of implicit conversions is provided in `body.Implicits` for convenience.

You can `post` or `put` some data with your favorite encoding.
```scala
import fr.hmil.roshttp.body.Implicits._
import fr.hmil.roshttp.body.URLEncodedBody

val urlEncodedData = URLEncodedBody(
  "answer" -> "42",
  "platform" -> "jvm"
)
request.post(urlEncodedData)
// or
request.put(urlEncodedData)
```

Create JSON requests easily using implicit conversions.
```scala
import fr.hmil.roshttp.body.Implicits._
import fr.hmil.roshttp.body.JSONBody._

val jsonData = JSONObject(
  "answer" -> 42,
  "platform" -> "node"
)
request.post(jsonData)
```

### File upload

To send file data you must turn a file into a ByteBuffer and then send it in a
ByteBufferBody. For instance, on the jvm you could do:
```scala
import java.nio.ByteBuffer
import fr.hmil.roshttp.body.ByteBufferBody

val buffer = ByteBuffer.wrap(
        List[Byte](0x68, 0x65, 0x6c, 0x6c, 0x6f, 0x20, 0x77, 0x6f, 0x72, 0x6c, 0x64, 0x0a)
        .toArray)
request.post(ByteBufferBody(buffer))
```
Note that the codec argument is important to read the file as-is and avoid side-effects
due to character interpretation.

### Multipart

Use the `MultiPartBody` to compose request bodies arbitrarily. It allows for instance
to send binary data with some textual data.

The following example illustrates how you could send a form to update a user profile
made of a variety of data types.
```scala
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
  // The picture is sent using a ByteBufferBody, assuming buffer is a ByteBuffer
  // containing the image data
  "picture" -> ByteBufferBody(buffer, "image/jpeg")
))
```

## Streaming

### Download streams

Streaming a response is as simple as calling `.stream()` instead of `.send()`.
`HttpRequest#stream()` returns a Future of `StreamHttpResponse`. A `StreamHttpResponse`
is just like a `SimpleHttpResponse` except that its `body` property is an
[Observable](https://monix.io/api/2.0/#monix.reactive.Observable).
The observable will spit out a stream of `ByteBuffer`s as shown in this example:

```scala
import fr.hmil.roshttp.util.Utils._

request
  .stream()
  .map({ r =>
    r.body.foreach(buffer => println(getStringFromBuffer(buffer, "UTF-8")))
  })
```
_Note that special care should be taken when converting chunks into strings because
multibyte characters may span multiple chunks._
_In general streaming is used for binary data and any reasonable quantity
of text can safely be handled by the non-streaming API._

#### HTTP methods

There is no shortcut method such as `.post` to get a streaming response. You can
still achieve that by using the constructor methods as shown below:
```scala
import fr.hmil.roshttp.Method.POST

request
  .withMethod(POST)
  .withBody(PlainTextBody("My upload data"))
  .stream()
  // The response will be streamed
```

### Upload Streams

There are cases where you want to upload some very large data with minimal memory
consumption. We've got you covered! The [StreamBody](TODO: doc link) takes an
[Observable](https://monix.io/api/2.0/#monix.reactive.Observable)[ByteBuffer]
and streams its contents to the server. You can also pass an InputStream directly
using RösHTTP's implicit converters:
<!-- Defining an inputStream for the tests
```scala
val inputStream = new java.io.ByteArrayInputStream(new Array[Byte](1))
```
-->
```scala
import fr.hmil.roshttp.body.Implicits._

// On the JVM:
// val inputStream = new java.io.FileInputStream("video.avi")
request
  .post(inputStream)
  .onComplete({
    case _:Success[SimpleHttpResponse] => println("Data successfully uploaded")
    case _:Failure[SimpleHttpResponse] => println("Error: Could not upload stream")
  })
```

---

Watch the [issues](https://github.com/hmil/RosHTTP/issues)
for upcoming features. Feedback is very welcome so feel free to file an issue if you
see something that is missing.

# Known limitations

- Streaming is emulated in the browser, meaning that streaming large request or
  response payloads in the browser will consume large amounts of memory and might fail.
  This [problem has a solution](https://github.com/hmil/RosHTTP/issues/46)
- `bodyCollectTimeout` is ignored on Chrome.
- Some headers cannot be set in the browser ([list](https://developer.mozilla.org/en-US/docs/Glossary/Forbidden_header_name)).
- There is no way to avoid redirects in the browser. This is a W3C spec.
- Chrome does not allow userspace handling of a 407 status code. It is treated
  like a network error. See [chromium issue](https://bugs.chromium.org/p/chromium/issues/detail?id=372136).
- The `TRACE` HTTP method does not work in browsers and `PATCH` does not work in the JVM.

# Contributing

Please read the [contributing guide](https://github.com/hmil/RosHTTP/blob/master/CONTRIBUTING.md).

# Changelog

**v2.0.0**

- Renamed withQueryArrayParameter to withQuerySeqParameter
- Timeout errors on body
- Rename *Error classes to *Exception
- Add streaming API
- Add implicit Scheduler parameter
- Add implicit execution context parameter

**v1.1.0**
- Fix bug on responses without Content-Type header
- Detect key-value pairs during query string escapement

**v1.0.1**
- Fix NPE when reading empty error response

**v1.0.0**
- Using [semantic versioning](http://semver.org/) from now on
- Renamed RösHTTP
- Add .withBody()

**v0.3.0**
- Remove general purpose StringBody
- Add missing patch method
- Make Method constructor public
- Disambiguate `withQueryArrayParameter` and `withQueryObjectParameter`
- Remove map parameters from `.withQueryParameter(s)` and `.withHeaders`

**v0.2.0**
- Support request body with `post()`, `put()` and `options()`
- Add `withHttpMethod()`
- Support HTTPS

**v0.1.0**
- First release

# License

MIT
