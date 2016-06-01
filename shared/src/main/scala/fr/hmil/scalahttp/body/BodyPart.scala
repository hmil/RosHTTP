package fr.hmil.scalahttp.body

import java.nio.ByteBuffer

trait BodyPart {
  def contentType: String
  def content: ByteBuffer
}
