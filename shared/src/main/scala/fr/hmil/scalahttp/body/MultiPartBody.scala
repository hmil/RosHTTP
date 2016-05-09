package fr.hmil.scalahttp.body

import scala.util.Random

class MultiPartBody(parts: Map[String, BodyPart]) extends BodyPart {
  // TODO doc
  val boundary = "----" + Random.alphanumeric.take(24).mkString.toLowerCase

  override val contentType: String = s"multipart/form-data; boundary=$boundary"

  override val content: String = {
    parts.map({case (name, part) =>
      "--" + boundary + "\r\n" +
      "Content-Disposition: form-data; name=\"" + name + "\"\r\n" +
      s"Content-Type: ${part.contentType}\r\n" +
      "\r\n" +
      part.content
    }).mkString("\r\n") +
      s"\r\n--$boundary--"
  }

  override val contentLength = content.length
}
