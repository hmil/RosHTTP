package fr.hmil.scalahttp.body

/** A simple UTF-8 encoded string body
  * @param text The enclosed string.
  */
class StringBody(
    text: String
  ) extends BodyPart {

  override val contentType: String = "text/plain; charset=utf-8"
  override val content: Array[Byte] = text.getBytes("utf-8")
}
