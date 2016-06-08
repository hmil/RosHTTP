package fr.hmil.roshttp.body

import java.nio.ByteBuffer

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
  * @param data The bytes to send
  * @param contentType
  */
class StreamBody private(
    data: ByteBuffer,
    override val contentType: String
  ) extends BodyPart {

  override def content: ByteBuffer = data
}

object StreamBody {
  def apply(data: ByteBuffer, contentType: String = "application/octet-stream"): StreamBody =
    new StreamBody(data, contentType)
}