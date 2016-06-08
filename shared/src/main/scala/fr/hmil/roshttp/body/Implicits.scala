package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import fr.hmil.roshttp.body.JSONBody._

object Implicits {
  implicit def stringToJSONString(value: String): JSONString = new JSONString(value)
  implicit def intToJSONNumber(value: Int): JSONNumber = new JSONNumber(value)
  implicit def floatToJSONNumber(value: Float): JSONNumber = new JSONNumber(value)
  implicit def doubleToJSONNumber(value: Double): JSONNumber = new JSONNumber(value)
  implicit def JSONObjectToJSONBody(obj: JSONObject): JSONBody = JSONBody(obj)
  implicit def byteBufferToStreamBody(buff: ByteBuffer): StreamBody = StreamBody(buff)
}
