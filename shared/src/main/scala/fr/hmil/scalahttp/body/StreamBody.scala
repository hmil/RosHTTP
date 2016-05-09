package fr.hmil.scalahttp.body

class StreamBody(
    stream: Stream[Char],
    override val contentType: String = "application/octet-stream"
  ) extends BodyPart {

  override val contentLength: Int = stream.length
  override val content: String = stream.mkString
}
