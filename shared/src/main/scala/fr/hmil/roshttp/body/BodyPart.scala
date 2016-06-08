package fr.hmil.roshttp.body

import java.nio.ByteBuffer

trait BodyPart {
  def contentType: String
  def content: ByteBuffer
}
