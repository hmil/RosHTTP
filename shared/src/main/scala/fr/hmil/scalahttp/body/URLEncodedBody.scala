package fr.hmil.scalahttp.body

import fr.hmil.scalahttp.CrossPlatformUtils

class URLEncodedBody(values: Map[String, String]) extends BodyPart {
  // TODO doc
  override val contentType: String = s"application/x-www-form-urlencoded"

  override val content: Array[Byte] = {
    values.map({case (name, part) =>
      CrossPlatformUtils.encodeQueryString(name) +
      "=" +
      CrossPlatformUtils.encodeQueryString(part)
    }).mkString("&").getBytes("utf-8")
  }

  override val contentLength = content.length
}
