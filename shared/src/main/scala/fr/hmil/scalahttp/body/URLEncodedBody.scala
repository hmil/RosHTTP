package fr.hmil.scalahttp.body

import fr.hmil.scalahttp.CrossPlatformUtils

class URLEncodedBody(parts: Map[String, BodyPart]) extends BodyPart {
  // TODO doc
  override val contentType: String = s"application/x-www-form-urlencoded"

  override val content: String = {
    parts.map({case (name, part) =>
        CrossPlatformUtils.encodeQueryString(name) +
        "=" +
        CrossPlatformUtils.encodeQueryString(part.content)
    }).mkString("&")
  }

  override val contentLength = content.length
}
