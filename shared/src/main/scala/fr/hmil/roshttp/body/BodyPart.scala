package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import monifu.reactive.Observable

trait BodyPart {
  def contentType: String
  def content: Observable[ByteBuffer]
}
