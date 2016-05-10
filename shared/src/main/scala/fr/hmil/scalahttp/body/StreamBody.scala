package fr.hmil.scalahttp.body

class StreamBody(
    stream: Stream[Byte],
    override val contentType: String = "application/octet-stream"
  ) extends BodyPart {

  override val contentLength: Int = stream.length
  override val content: Array[Byte] = {
    val acc = new Array[Byte](stream.length)
    stream.foldLeft(0)({
      case (i, b) => acc(i) = b; i + 1
    })
    acc
  }
}
