package fr.hmil.scalahttp.body

import fr.hmil.scalahttp.body.JSONBody.JSONValue

/** Experimental support for JSON-encoded bodies.
  *
  * Allows to send arbitrarily complex JSON data.
  *
  * @param value The JSON value to send.
  */
class JSONBody(value: JSONValue) extends BodyPart {
  override val contentType: String = s"application/json; charset=utf-8"

  override val content: Array[Byte] = value.toString.getBytes("utf-8")
}

object JSONBody {
  trait JSONValue {
    def toString: String
  }

  class JSONNumber(value: Number) extends JSONValue {
    override def toString: String = value.toString
  }

  class JSONString(value: String) extends JSONValue {
    override def toString: String = "\"" + escapeString(value) + "\""
  }

  class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    override def toString: String = {
      "{" +
        values.map({case (name, part) =>
          "\"" + escapeString(name) + "\"" +
          ":" + part
        }).mkString(",") +
      "}"
    }
  }

  // TODO: make this spec-compliant
  private def escapeString(str: String): String = {
    str.replace("\\", "\\\\").replace("\"", "\\\"")
  }


  def apply(value: JSONValue): JSONBody = new JSONBody(value)

  implicit def mapToJSONObject(values: Map[String, JSONValue]): JSONValue = new JSONObject(values)
  implicit def stringToJSONString(value: String): JSONString = new JSONString(value)
  implicit def intToJSONNumber(value: Int): JSONNumber = new JSONNumber(value)
  implicit def floatToJSONNumber(value: Float): JSONNumber = new JSONNumber(value)
  implicit def doubleToJSONNumber(value: Double): JSONNumber = new JSONNumber(value)
}
