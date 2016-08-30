package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import monifu.reactive.Observable

class StreamBody private(val data: Observable[ByteBuffer]) extends BodyPart {

    override def contentType: String = s"application/x-www-form-urlencoded"

    override def content: Observable[ByteBuffer] = data
}

object StreamBody {
    def apply(data: Observable[ByteBuffer]): StreamBody = new StreamBody(data)
}