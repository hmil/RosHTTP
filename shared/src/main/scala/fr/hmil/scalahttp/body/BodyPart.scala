package fr.hmil.scalahttp.body

import java.nio.ByteBuffer

trait BodyPart {
  val contentType: String
  val content: ByteBuffer
}
