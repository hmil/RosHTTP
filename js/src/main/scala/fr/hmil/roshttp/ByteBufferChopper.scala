package fr.hmil.roshttp

import java.nio.ByteBuffer


object ByteBufferChopper {

  def chop(buffer: ByteBuffer, maxChunkSize: Int): Seq[ByteBuffer] = {
    val nb_buffers = (buffer.limit + maxChunkSize - 1) / maxChunkSize
    val buffers = new Array[ByteBuffer](nb_buffers)
    var i = 0
    while (i < nb_buffers) {
      val length = Math.min(maxChunkSize, buffer.remaining)
      buffers(i) = buffer.slice()
      buffers(i).limit(length)
      buffer.position(buffer.position + length)
      i = i + 1
    }
    buffers
  }

}
