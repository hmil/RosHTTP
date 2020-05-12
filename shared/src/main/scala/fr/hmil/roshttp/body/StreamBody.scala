package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import monix.reactive.Observable

class StreamBody private(
    override val content: Observable[ByteBuffer],
    override val contentType: String
) extends BodyPart

object StreamBody {
    def apply(data: Observable[ByteBuffer], contentType: String = "application/octet-stream"): StreamBody =
        new StreamBody(data, contentType)
}
