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
libraryDependencies += "fr.hmil" %%% "roshttp" % "1.0.1"
```

# Usage

The following is a simplified usage guide. You may find useful information in
the [API doc](http://hmil.github.io/RosHTTP/docs/index.html) too.
## Basic usage

<!--- test: "Main example" -->
```scala
import fr.hmil.roshttp.HttpRequest
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
[HttpRequest](http://hmil.github.io/RosHTTP/docs/index.html#fr.hmil.roshttp.HttpRequest)
offers an API to add, update and delete keys in the query string.  

<!--- test: "Query parameters" -->
```scala
request
  .withQueryParameter("foo", "bar")
  .withQueryArrayParameter("table", "a", "b", "c")
  .withQueryObjectParameter("map",
    "d" -> "dval",
    "e" -> "e value"
  )
  .withQueryParameters(
    "license" -> "MIT",
    "copy" -> "© 2016"
  )
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
request.withHeaders(
  "Accept" -> "text/html",
  "Cookie" -> "sessionid=f00ba242cafe"
)
```

### Response headers

A map of response headers is available on the [[HttpResponse]] object:
```scala
request.send().map({res =>
  println(res.headers("Set-Cookie"))
})
```

### Sending data

An HTTP request can send data wrapped in an implementation of `BodyPart`. The most common
formats are already provided but you can create your own as well.   
A set of implicit conversions is provided in `body.Implicits` for convenience.

You can `post` or `put` some data with your favorite encoding.
```scala
import fr.hmil.roshttp.body.Implicits._

val data = URLEncodedBody(
  "answer" -> "42",
  "platform" -> "jvm"
)
request.post(data)
// or
request.put(data)
```

Create JSON requests easily using implicit conversions.
```scala
import fr.hmil.roshttp.body.Implicits._

val data = JSONObject(
  "answer" -> 42,
  "platform" -> "node"
)
request.post(data)
```

#### File upload

To send file data you must turn a file into a ByteBuffer and then send it in a
StreamBody. For instance, on the jvm you could do:
```
import fr.hmil.roshttp.body.Implicits._

val bytes = Source.fromFile("icon.png")(scala.io.Codec.ISO8859).map(_.toByte).toArray
request.post(ByteBuffer.wrap(bytes))
```
Note that the codec argument is important to read the file as-is and avoid side-effects
due to character interpretation.

#### Multipart

Use the `MultiPartBody` to compose request bodies arbitrarily. It allows for instance
to send binary data with some textual data.

The following example illustrates how you could send a form to update a user profile
made of a variety of data types.
```scala
import fr.hmil.roshttp.body.Implicits._

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
  // The picture is sent using a StreamBody, assuming image_bytes is a ByteBuffer containing the image
  "picture" -> StreamBody(image_bytes, "image/jpeg")
))
```

### HTTP Method

```scala
// Set the request method to GET, POST, PUT, etc...
request.withMethod(Method.PUT).send()
```

---

Watch the [issues](https://github.com/hmil/RosHTTP/issues)
for upcoming features. Feedback is very welcome so feel free to file an issue if you
see something that is missing.

## Known limitations

- Some headers cannot be set in the browser ([list](https://developer.mozilla.org/en-US/docs/Glossary/Forbidden_header_name)).
- There is no way to avoid redirects in the browser. This is a W3C spec.
- Chrome does not allow userspace handling of a 407 status code. It is treated
  like a network error. See [chromium issue](https://bugs.chromium.org/p/chromium/issues/detail?id=372136).
- The `TRACE` HTTP method does not work in browsers and `PATCH` does not work in the JVM.

## Contributing

Please read the [contributing guide](https://github.com/hmil/RosHTTP/blob/master/CONTRIBUTING.md).

## Changelog

- Fix bug on responses without Content-Type header

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

## License

MIT
