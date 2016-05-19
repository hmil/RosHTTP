package fr.hmil.scalahttp.body

import java.nio.ByteBuffer

import scala.util.Random

/** A body made of multiple parts.
  *
  * <b>Usage:</b> A multipart body acts as a container for other bodies. For instance,
  * the multipart body is commonly used to send a form with binary attachments in conjunction with
  * the [[StreamBody]].
  * For simple key/value pairs, use [[URLEncodedBody]] instead.
  *
  * <b>Safety consideration:</b> A random boundary is generated to separate parts. If the boundary was
  * to occur within a body part, it would mess up the whole body. In practice, the odds are extremely small though.
  *
  * @param parts The pieces of body. The key in the map is used as `name` for the `Content-Disposition` header
  *              of each part.
  * @param subtype The exact multipart mime type as in `multipart/subtype`. Defaults to `form-data`.
  */
class MultiPartBody private(parts: Map[String, BodyPart], subtype: String = "form-data") extends BodyPart {

  val boundary = "----" + Random.alphanumeric.take(24).mkString.toLowerCase

  override val contentType: String = s"multipart/$subtype; boundary=$boundary"

  override val content: ByteBuffer = {
    ByteBuffer.wrap((
      parts.map({case (name, part) =>
        "--" + boundary + "\r\n" +
        "Content-Disposition: form-data; name=\"" + name + "\"\r\n" +
        s"Content-Type: ${part.contentType}\r\n" +
        "\r\n" +
        new String(part.content.array(), "utf-8")
      }).mkString("\r\n") +
        s"\r\n--$boundary--"
    ).getBytes("utf-8"))
  }
}

object MultiPartBody {
  def apply(parts: (String, BodyPart)*): MultiPartBody = new MultiPartBody(Map(parts: _*))
}
