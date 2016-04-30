package fr.hmil.scalahttp

import scala.io.Source
import scala.util.Random


package object body {

  trait BodyPart {
    val contentType: String
    val contentLength: Int
    val content: String
  }

  class TextPart(
        text: String,
        override val contentType: String = "text/plain; charset=utf-8")
      extends BodyPart {
    override val contentLength: Int = text.length
    override val content: String = text
  }

  class StreamPart(
        stream: Stream[Char],
        override val contentType: String = "application/octet-stream")
      extends BodyPart {

    override val contentLength: Int = stream.length
    override val content: String = stream.mkString
  }

  // Crude implementation of multipart/form-data body type
  class MultiPart(parts: Map[String, BodyPart]) extends BodyPart {
    private val boundary = "--------------------------" + Random.alphanumeric.take(12)

    override val contentType: String = s"multipart/form-data; boundary=$boundary"

    override val content: String = {
      parts.map({case (name, part) =>
        boundary + "\r\n" +
          "Content-Disposition: form-data; name=\"" + name + "\"\r\n" +
          s"Content-Type: ${part.contentType}\r\n" +
          "\r\n" +
          part.content

      }).mkString("\r\n") +
        s"\r\n$boundary--"
    }

    override val contentLength = content.length

  }

  object Implicits {

    implicit def stringToTextPart(s: String): TextPart = new TextPart(s)
  }
}
