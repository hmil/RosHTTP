package fr.hmil.scalahttp.body

import java.nio.ByteBuffer

/** A simple UTF-8 encoded string body
  *
  * @param text The enclosed string.
  */
class StringBody private(
    text: String
  ) extends BodyPart {

  override def contentType: String = "text/plain; charset=utf-8"
  override def content: ByteBuffer = ByteBuffer.wrap(text.getBytes("utf-8"))
}

object StringBody {
  def apply(text: String): StringBody = new StringBody(text)
}