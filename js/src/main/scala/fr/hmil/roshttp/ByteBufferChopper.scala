package fr.hmil.roshttp

import java.nio.ByteBuffer


object ByteBufferChopper {

  def chop[T <: Finite](buffer: T, maxChunkSize: Int, read: (T, Int, Int) => ByteBuffer): Seq[ByteBuffer] = {
    val nb_buffers = (buffer.length + maxChunkSize - 1) / maxChunkSize
    val buffers = new Array[ByteBuffer](nb_buffers)
    var currentPosition = 0
    var i = 0
    while (i < nb_buffers) {
      val length = Math.min(maxChunkSize, buffer.length - currentPosition)
      buffers(i) = read(buffer, currentPosition, length)
      currentPosition += length
      i = i + 1
    }
    buffers
  }

  trait Finite {
    def length: Int
  }
}
