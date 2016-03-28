# Scala http client

A human-readable scala http client API compatible with:

- vanilla jvm **scala**
- most **browsers** (_via_ [scala-js](https://github.com/scala-js/scala-js))
- **node.js** (_via_ [scala-js](https://github.com/scala-js/scala-js))

## Installation

WIP, this package is not published yet

## Usage

```scala
// Runs consistently on the jvm, in node.js and in the browser!
HttpRequest("http://www.scala-lang.org/")
  .send()
  .map(response => println(response.body))
```

TODO: full usage doc

## Known limitations

- There is no way to avoid redirects in the browser. This is a W3C spec.
- Chrome does not allow userspace handling of a 407 status code (ie: it is treated
  like a network error). see https://bugs.chromium.org/p/chromium/issues/detail?id=372136

## Changelog
