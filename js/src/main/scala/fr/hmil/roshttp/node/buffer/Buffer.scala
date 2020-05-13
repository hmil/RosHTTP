package fr.hmil.roshttp.node.buffer

import scala.scalajs.js
import js.annotation._


/**
  * Nodejs Buffer API
 *
  * @see https://nodejs.org/api/buffer.html
  */
@js.native
@JSGlobal
private[roshttp] class Buffer extends js.Object {
  // new Buffer(array)
  // new Buffer(buffer)
  // new Buffer(arrayBuffer)
  // new Buffer(size)
  // new Buffer(str[, encoding])
  // Class Method: Buffer.byteLength(string[, encoding])
  // Class Method: Buffer.compare(buf1, buf2)
  // Class Method: Buffer.concat(list[, totalLength])
  // Class Method: Buffer.isBuffer(obj)
  // Class Method: Buffer.isEncoding(encoding)

  @JSBracketAccess
  def apply(index: Int): Byte = js.native
  @JSBracketAccess
  def update(index: Int, v: Byte): Unit = js.native

  def compare(otherBuffer: Buffer): Int = js.native

  def copy(targetBuffer: Buffer): Int = js.native
  def copy(targetBuffer: Buffer, targetStart: Int): Int = js.native
  def copy(targetBuffer: Buffer, targetStart: Int, sourceStart: Int): Int = js.native
  def copy(targetBuffer: Buffer, targetStart: Int, sourceStart: Int, sourceEnd: Int): Int = js.native

  // buf.entries()

  // override def equals(otherBuffer: Buffer): Boolean = js.native

  def fill(value: String): Buffer = js.native
  def fill(value: Buffer): Buffer = js.native
  def fill(value: Int): Buffer = js.native
  def fill(value: String, encoding: String): Buffer = js.native
  def fill(value: Buffer, encoding: String): Buffer = js.native
  def fill(value: Int, encoding: String): Buffer = js.native
  def fill(value: String, offset: Int): Buffer = js.native
  def fill(value: Buffer, offset: Int): Buffer = js.native
  def fill(value: Int, offset: Int): Buffer = js.native
  def fill(value: String, offset: Int, encoding: String): Buffer = js.native
  def fill(value: Buffer, offset: Int, encoding: String): Buffer = js.native
  def fill(value: Int, offset: Int, encoding: String): Buffer = js.native
  def fill(value: String, offset: Int, end: Int): Buffer = js.native
  def fill(value: Buffer, offset: Int, end: Int): Buffer = js.native
  def fill(value: Int, offset: Int, end: Int): Buffer = js.native
  def fill(value: String, offset: Int, end: Int, encoding: String): Buffer = js.native
  def fill(value: Buffer, offset: Int, end: Int, encoding: String): Buffer = js.native
  def fill(value: Int, offset: Int, end: Int, encoding: String): Buffer = js.native

  def indexOf(value: String): Int = js.native
  def indexOf(value: Buffer): Int = js.native
  def indexOf(value: Int): Int = js.native
  def indexOf(value: String, byteOffset: Int): Int = js.native
  def indexOf(value: Buffer, byteOffset: Int): Int = js.native
  def indexOf(value: Int, byteOffset: Int): Int = js.native
  def indexOf(value: String, byteOffset: Int, encoding: String): Int = js.native
  def indexOf(value: Buffer, byteOffset: Int, encoding: String): Int = js.native
  def indexOf(value: Int, byteOffset: Int, encoding: String): Int = js.native

  def includes(value: String): Boolean = js.native
  def includes(value: Buffer): Boolean = js.native
  def includes(value: Int): Boolean = js.native
  def includes(value: String, byteOffset: Int): Boolean = js.native
  def includes(value: Buffer, byteOffset: Int): Boolean = js.native
  def includes(value: Int, byteOffset: Int): Boolean = js.native
  def includes(value: String, byteOffset: Int, encoding: String): Boolean = js.native
  def includes(value: Buffer, byteOffset: Int, encoding: String): Boolean = js.native
  def includes(value: Int, byteOffset: Int, encoding: String): Boolean = js.native

  // buf.keys()

  val length: Int = js.native

  def readDoubleBE(offset: Double): Int = js.native
  def readDoubleBE(offset: Double, noAssert: Boolean): Int = js.native

  def readDoubleLE(offset: Double): Int = js.native
  def readDoubleLE(offset: Double, noAssert: Boolean): Int = js.native

  def readFloatBE(offset: Float): Int = js.native
  def readFloatBE(offset: Float, noAssert: Boolean): Int = js.native

  def readFloatLE(offset: Float): Int = js.native
  def readFloatLE(offset: Float, noAssert: Boolean): Int = js.native

  def readInt8(offset: Int): Int = js.native
  def readInt8(offset: Int, noAssert: Boolean): Int = js.native

  def readInt16BE(offset: Int): Int = js.native
  def readInt16BE(offset: Int, noAssert: Boolean): Int = js.native

  def readInt16LE(offset: Int): Int = js.native
  def readInt16LE(offset: Int, noAssert: Boolean): Int = js.native

  def readInt32BE(offset: Int): Int = js.native
  def readInt32BE(offset: Int, noAssert: Boolean): Int = js.native

  def readInt32LE(offset: Int): Int = js.native
  def readInt32LE(offset: Int, noAssert: Boolean): Int = js.native

  def readIntBE(offset: Int): Int = js.native
  def readIntBE(offset: Int, noAssert: Boolean): Int = js.native

  def readIntLE(offset: Int): Int = js.native
  def readIntLE(offset: Int, noAssert: Boolean): Int = js.native

  def readUInt8(offset: Int): Int = js.native
  def readUInt8(offset: Int, noAssert: Boolean): Int = js.native

  def readUInt16BE(offset: Int): Int = js.native
  def readUInt16BE(offset: Int, noAssert: Boolean): Int = js.native

  def readUInt16LE(offset: Int): Int = js.native
  def readUInt16LE(offset: Int, noAssert: Boolean): Int = js.native

  def readUInt32BE(offset: Int): Int = js.native
  def readUInt32BE(offset: Int, noAssert: Boolean): Int = js.native

  def readUInt32LE(offset: Int): Int = js.native
  def readUInt32LE(offset: Int, noAssert: Boolean): Int = js.native

  def readUIntBE(offset: Int): Int = js.native
  def readUIntBE(offset: Int, noAssert: Boolean): Int = js.native

  def readUIntLE(offset: Int): Int = js.native
  def readUIntLE(offset: Int, noAssert: Boolean): Int = js.native

  def slice(): Buffer = js.native
  def slice(start: Int): Buffer = js.native
  def slice(start: Int, end: Int): Buffer = js.native

  override def toString(): String = js.native
  def toString(encoding: String): String = js.native
  def toString(encoding: String, start: Int): String = js.native
  def toString(encoding: String, start: Int, end: Int): String = js.native

  def toJSON(): js.Object = js.native

  // buf.values()

  def write(string: String): Int = js.native
  def write(string: String, encoding: String): Int = js.native
  def write(string: String, offset: Int): Int = js.native
  def write(string: String, offset: Int, encoding: String): Int = js.native
  def write(string: String, offset: Int, length: Int): Int = js.native
  def write(string: String, offset: Int, length: Int, encoding: String): Int = js.native

  def writeDoubleBE(value: Double, offset: Int): Int = js.native
  def writeDoubleBE(value: Double, offset: Int, noAssert: Boolean): Int = js.native

  def writeDoubleLE(value: Double, offset: Int): Int = js.native
  def writeDoubleLE(value: Double, offset: Int, noAssert: Boolean): Int = js.native

  def writeFloatBE(value: Float, offset: Int): Int = js.native
  def writeFloatBE(value: Float, offset: Int, noAssert: Boolean): Int = js.native

  def writeFloatLE(value: Float, offset: Int): Int = js.native
  def writeFloatLE(value: Float, offset: Int, noAssert: Boolean): Int = js.native

  def writeInt8(value: Int, offset: Int): Int = js.native
  def writeInt8(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeInt16BE(value: Int, offset: Int): Int = js.native
  def writeInt16BE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeInt16LE(value: Int, offset: Int): Int = js.native
  def writeInt16LE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeInt32BE(value: Int, offset: Int): Int = js.native
  def writeInt32BE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeInt32LE(value: Int, offset: Int): Int = js.native
  def writeInt32LE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeIntBE(value: Int, offset: Int): Int = js.native
  def writeIntBE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeIntLE(value: Int, offset: Int): Int = js.native
  def writeIntLE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeUInt8(value: Int, offset: Int): Int = js.native
  def writeUInt8(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeUInt16BE(value: Int, offset: Int): Int = js.native
  def writeUInt16BE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeUInt16LE(value: Int, offset: Int): Int = js.native
  def writeUInt16LE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeUInt32BE(value: Int, offset: Int): Int = js.native
  def writeUInt32BE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeUInt32LE(value: Int, offset: Int): Int = js.native
  def writeUInt32LE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeUIntBE(value: Int, offset: Int): Int = js.native
  def writeUIntBE(value: Int, offset: Int, noAssert: Boolean): Int = js.native

  def writeUIntLE(value: Int, offset: Int): Int = js.native
  def writeUIntLE(value: Int, offset: Int, noAssert: Boolean): Int = js.native
}
