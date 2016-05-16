package fr.hmil.scalahttp.body

/** A body containing raw binary data
  *
  * <b>Usage:</b> Stream bodies are used to send arbitrary binary data such as
  * audio, video or any other file attachment.
  * The content-type can be overridden to something more specific like `image/jpeg`
  * or `audio/wav` for instance.
  * It is common to embed a stream body in a [[MultiPartBody]] to send additional information
  * with the binary file.
  * When possible, send a `Content-Length` header along with an octet-stream body. It may allow the receiver
  * end to better handle the loading.
  *
  * A stream body is sent with the content-type application/octet-stream.
  *
  * @param stream The Stream of bytes to send
  * @param contentType
  */
class StreamBody(
    stream: Stream[Byte],
    override val contentType: String = "application/octet-stream"
  ) extends BodyPart {

  override val content: Array[Byte] = {
    val acc = new Array[Byte](stream.length)
    stream.foldLeft(0)({
      case (i, b) => acc(i) = b; i + 1
    })
    acc
  }
}
