package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import monix.reactive.Observable

trait BodyPart {
  def contentType: String
  def content: Observable[ByteBuffer]
}
