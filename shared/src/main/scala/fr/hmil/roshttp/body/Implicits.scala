package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import org.json4s.ast.safe.JValue

object Implicits {
  implicit def JValueToJSONBody(obj: JValue): JSONBody = JSONBody(obj)
  implicit def byteBufferToStreamBody(buff: ByteBuffer): StreamBody = StreamBody(buff)
}
