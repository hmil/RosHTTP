package fr.hmil.roshttp.body

import java.nio.ByteBuffer

/** Plain text body sent as `text/plain` mime type.
  *
  * @param text The plain text to send
  * @param charset Charset used for encoded (defaults to utf-8)
  */
class PlainTextBody private(
    text: String,
    charset: String
  ) extends BodyPart {

  override def contentType: String = "text/plain; charset=" + charset
  override def content: ByteBuffer = ByteBuffer.wrap(text.getBytes(charset))
}

object PlainTextBody {
  def apply(text: String, charset: String = "utf-8"): PlainTextBody = new PlainTextBody(text, charset)
}