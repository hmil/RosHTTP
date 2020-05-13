package fr.hmil.roshttp

import java.nio.ByteBuffer

import fr.hmil.roshttp.node.buffer.Buffer

import scala.scalajs.js
import scala.scalajs.js.typedarray.{ArrayBuffer, Int8Array, TypedArrayBuffer, Uint8Array}
import scala.scalajs.js.JSConverters._
import js.typedarray.TypedArrayBufferOps._

private object Converters {
  def byteArrayToUint8Array(arr: Array[Byte]): Uint8Array = {
    js.Dynamic.newInstance(js.Dynamic.global.Uint8Array)(arr.toJSArray).asInstanceOf[Uint8Array]
  }

  def byteBufferToNodeBuffer(buffer: ByteBuffer): Buffer = {
    if (buffer.isDirect) {
      js.Dynamic.newInstance(js.Dynamic.global.Buffer)(buffer.arrayBuffer).asInstanceOf[Buffer]
    } else if (buffer.hasArray) {
      js.Dynamic.newInstance(js.Dynamic.global.Buffer)(byteArrayToUint8Array(buffer.array())).asInstanceOf[Buffer]
    } else {
      val arr = new Int8Array(buffer.limit())
      var i = 0
      while (i < arr.length) {
        arr(i) = buffer.get(i)
        i += 1
      }
      js.Dynamic.newInstance(js.Dynamic.global.Buffer)(arr).asInstanceOf[Buffer]
    }
  }

  def nodeBufferToByteBuffer(buffer: Buffer): ByteBuffer = {
    TypedArrayBuffer.wrap(buffer.asInstanceOf[ArrayBuffer])
  }

  def arrayBufferToByteBuffer(buffer: ArrayBuffer): ByteBuffer = {
    TypedArrayBuffer.wrap(buffer)
  }
}
