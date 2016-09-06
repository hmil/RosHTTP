package fr.hmil.roshttp.body
import java.nio.ByteBuffer

import monifu.reactive.Observable

abstract class BulkBodyPart extends BodyPart {
  override def content: Observable[ByteBuffer] = Observable.from(contentData)
  def contentData: ByteBuffer
}
