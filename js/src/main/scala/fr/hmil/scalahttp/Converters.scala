package fr.hmil.scalahttp

import java.nio.ByteBuffer

import fr.hmil.scalahttp.node.buffer.Buffer

import scala.scalajs.js
import scala.scalajs.js.typedarray.Uint8Array
import scala.scalajs.js.JSConverters._

private object Converters {
  def byteArrayToUint8Array(arr: Array[Byte]): Uint8Array = {
    js.Dynamic.newInstance(js.Dynamic.global.Uint8Array)(arr.toJSArray).asInstanceOf[Uint8Array]
  }

  def byteBufferToNodeBuffer(buff: ByteBuffer): Buffer = {
    js.Dynamic.newInstance(js.Dynamic.global.Buffer)(byteArrayToUint8Array(buff.array())).asInstanceOf[Buffer]
  }
}
