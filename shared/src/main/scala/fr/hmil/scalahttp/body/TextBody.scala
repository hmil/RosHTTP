package fr.hmil.scalahttp.body


class TextBody(
    text: String,
    override val contentType: String = "text/plain; charset=utf-8"
  ) extends BodyPart {
  override val contentLength: Int = text.length
  override val content: Array[Byte] = text.getBytes("utf-8")
}
