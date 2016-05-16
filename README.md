# Scala http client
[![Build Status](https://travis-ci.org/hmil/scala-http-client.svg?branch=master)]
(https://travis-ci.org/hmil/scala-http-client)
[![Latest release](https://hmil.github.io/scala-http-client/version-badge.svg)]
(https://github.com/hmil/scala-http-client)

A human-readable scala http client API compatible with:

- vanilla jvm **scala**
- most **browsers** (_via_ [scala-js](https://github.com/scala-js/scala-js))
- **node.js** (_via_ [scala-js](https://github.com/scala-js/scala-js))

# Installation

Add a dependency in your build.sbt:

```scala
libraryDependencies += "fr.hmil" %%% "scala-http-client" % "0.1.0"
```

# Usage

The following is a simplified usage guide. You may find useful information in
the [API doc](http://hmil.github.io/scala-http-client/docs/index.html) too.
## Basic usage

<!--- test: "Main example" -->
```scala
import fr.hmil.scalahttp.client.HttpRequest
import scala.concurrent.ExecutionContext.Implicits.global

/* ... */

// Runs consistently on the jvm, in node.js and in the browser!
val request = HttpRequest("https://schema.org/WebPage")

request.send().map(response => println(response.body))
```

When you `send()` a request, you get a `Future[HttpResponse]` which resolves to
an HttpResponse if everything went fine or fails with an HttpException if a
network error occurred or if a statusCode > 400 was received.
When applicable, the response body of a failed request can be read:

<!--- test: "Error handling" -->
```scala
HttpRequest("http://hmil.github.io/foobar")
  .send()
  .onFailure {
    case e:HttpResponseError =>
      s"Got a status: ${e.response.statusCode}" ==> "Got a status: 404"
  }
```


## Configuring requests

Every aspect of a request can be customized using `.withXXX` methods. These are
meant to be chained, they do not modify the original request.

### URI

The URI can be built using `.withProtocol`, `.withHost`, `.withPort`,
`.withPath`, and `.withQuery...`. The latter is a bit more complex and
is detailed below.

<!--- test: "Composite URI" -->
```scala
HttpRequest()
  .withProtocol("HTTP")
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
[HttpRequest](http://hmil.github.io/scala-http-client/docs/index.html#fr.hmil.scalahttp.client.HttpRequest)
offers an API to add, update and delete keys in the query string.  

<!--- test: "Query parameters" -->
```scala
request
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
  /* Query is now:
   foo=bar&table=a&table=b&table=c&map[d]=dval&map[e]=e%20value&license=MIT&copy=%C2%A9%202016
  */
```

### Request headers

Set individual headers using `.withHeader`
```scala
request.withHeader("Accept", "text/html")
```
Or multiple headers at once using `.withHeaders`
```scala
request.withHeaders(Map(
  "Accept" -> "text/html",
  "Cookie" -> "sessionid=f00ba242cafe"
))
```

### Response headers

A map of response headers is available on the [[HttpResponse]] object:
```scala
request.send().map({res =>
  println(res.headers("Set-Cookie"))
})
```

### Sending data

You can `post` or `put` some data with your favorite encoding.
```scala
val data = new URLEncodedBody(Map(
  "answer" -> "42",
  "platform" -> "jvm"
))
request.post(data)
// or
request.put(data)
```

Create JSON requests easily using implicit conversions.
```scala
import fr.hmil.scalahttp.body.JSONBody._

val data = JSONBody(new JSONObject(Map(
  "answer" -> 42,
  "platform" -> "node"
)))
request.post(data)
```

#### File upload

To send file data you must turn a file into a stream of bytes and then send it in a
StreamBody. For instance, on the jvm you could do:
```
val stream = Source.fromFile("icon.png")(scala.io.Codec.ISO8859).map(_.toByte).toStream
request.post(new StreamBody(stream))
```
Note that the codec argument is important to read the file as-is and avoid side-effects
due to character interpretation.


### HTTP Method

```scala
// Set the request method to GET, POST, PUT, etc...
request.withMethod(Method.PUT).send()
// OR use strings directly with implicit conversions
import fr.hmil.scalahttp.Method.Implicits._
request.withMethod("PUT").send()
```

---

Watch the [issues](https://github.com/hmil/scala-http-client/issues)
for upcoming features. Feedback is very welcome so feel free to file an issue if you
see something that is missing.

## Known limitations

- Some headers cannot be set in the browser ([list](https://developer.mozilla.org/en-US/docs/Glossary/Forbidden_header_name)).
- There is no way to avoid redirects in the browser. This is a W3C spec.
- Chrome does not allow userspace handling of a 407 status code. It is treated
  like a network error. See [chromium issue](https://bugs.chromium.org/p/chromium/issues/detail?id=372136).
- The `TRACE` HTTP method is unavailable in browsers.

## Changelog

**v0.2.0**
- support request body with `post()`, `put()` and `options()`
- add `withHttpMethod()`
- support HTTPS

**v0.1.0**
- First release
