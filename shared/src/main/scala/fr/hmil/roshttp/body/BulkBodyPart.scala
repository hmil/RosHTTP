package fr.hmil.roshttp.body
import java.nio.ByteBuffer

import monix.reactive.Observable

abstract class BulkBodyPart extends BodyPart {
  override def content: Observable[ByteBuffer] = Observable.eval(contentData)
  def contentData: ByteBuffer
}
