package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import monix.execution.Scheduler
import monix.reactive.Observable

import scala.util.Random

/** A body made of multiple parts.
  *
  * <b>Usage:</b> A multipart body acts as a container for other bodies. For instance,
  * the multipart body is commonly used to send a form with binary attachments in conjunction with
  * the [[ByteBufferBody]].
  * For simple key/value pairs, use [[URLEncodedBody]] instead.
  *
  * <b>Safety consideration:</b> A random boundary is generated to separate parts. If the boundary was
  * to occur within a body part, it would mess up the whole body. In practice, the odds are extremely small though.
  *
  * @param parts The pieces of body. The key in the map is used as `name` for the `Content-Disposition` header
  *              of each part.
  * @param subtype The exact multipart mime type as in `multipart/subtype`. Defaults to `form-data`.
  */
class MultiPartBody(parts: Map[String, BodyPart], subtype: String = "form-data")(implicit scheduler: Scheduler)
  extends BodyPart {

  val boundary = "----" + Random.alphanumeric.take(24).mkString.toLowerCase

  override def contentType: String = s"multipart/$subtype; boundary=$boundary"

  override def content: Observable[ByteBuffer] = {
    parts.
      // Prepend multipart encapsulation boundary and body part headers to
      // each body part.
      map({ case (name, part) =>
        ByteBuffer.wrap(
          ("\r\n--" + boundary + "\r\n" +
            "Content-Disposition: form-data; name=\"" + name + "\"\r\n" +
            s"Content-Type: ${part.contentType}\r\n" +
            "\r\n").getBytes("utf-8")
        ) +: part.content
      }).
      // Join body parts
      reduceLeft((acc, elem) => acc ++ elem).
      // Append the closing boundary
      :+(ByteBuffer.wrap(s"\r\n--$boundary--".getBytes("utf-8")))
  }
}

object MultiPartBody {
  def apply(parts: (String, BodyPart)*)(implicit scheduler: Scheduler): MultiPartBody =
    new MultiPartBody(Map(parts: _*))
}
