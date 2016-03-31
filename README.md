# Scala http client 
[![Build Status](https://travis-ci.org/hmil/scala-http-client.svg?branch=master)](https://travis-ci.org/hmil/scala-http-client)

A human-readable scala http client API compatible with:

- vanilla jvm **scala**
- most **browsers** (_via_ [scala-js](https://github.com/scala-js/scala-js))
- **node.js** (_via_ [scala-js](https://github.com/scala-js/scala-js))

## Installation

WIP, this package is not published yet

## Usage

Basic usage:
```scala
// Runs consistently on the jvm, in node.js and in the browser!
HttpRequest("http://schema.org/WebPage")
  .send()
  .map(response => println(response.body))
```

When you `send()` a request, you get a `Future[HttpResponse]` which resolves to an
HttpResponse if everything went fine or fails with an HttpException if a network error
occurred or if a statusCode > 400 was received.
When applicable, the response body of a failed request can be read:

```scala
HttpRequest("http://hmil.github.io/foobar")
  .send()
  .onFailure {
    case e:HttpException if e.response.isDefined =>
      println(s"Got a status: ${e.response.get.statusCode}")
      // Repsonse body is available at: e.response.get.body
  }
```

Requests are immutable but they can be customized using `.withXXX` methods (these
return a new request, they do not modify the original).

Example:
```scala
val baseRequest HttpRequest("http://localhost/")
val homeRequest = baseRequest withPort 3000 withPath "/home"

baseRequest.send() // queries "localhost/"
homeRequest.send() // queries "localhost:3000/home"
```

More features coming soon.

Read the [API doc](http://hmil.github.io/scala-http-client/docs/index.html) for further details.

## Known limitations

- There is no way to avoid redirects in the browser. This is a W3C spec.
- Chrome does not allow userspace handling of a 407 status code (ie: it is treated
  like a network error). see https://bugs.chromium.org/p/chromium/issues/detail?id=372136

## Changelog
