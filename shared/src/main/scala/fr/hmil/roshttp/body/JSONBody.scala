package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import fr.hmil.roshttp.body.JSONBody.JSONValue

/** Allows to send arbitrarily complex JSON data.
  *
  * @param value The JSON value to send.
  */
class JSONBody private(value: JSONValue) extends BulkBodyPart {
  override def contentType: String = s"application/json; charset=utf-8"

  override def contentData: ByteBuffer = ByteBuffer.wrap(value.toString.getBytes("utf-8"))
}

object JSONBody {
  trait JSONValue {
    def toString: String
  }

  class JSONNumber(value: Number) extends JSONValue {
    override def toString: String = value.toString
  }

  class JSONString(value: String) extends JSONValue {
    override def toString: String = "\"" + escapeJS(value) + "\""
  }

  class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    override def toString: String = {
      "{" +
        values.map({case (name, part) =>
          "\"" + escapeJS(name) + "\"" +
          ":" + part
        }).mkString(",") +
      "}"
    }
  }

  object JSONObject {
    def apply(values: (String, JSONValue)*): JSONObject = new JSONObject(Map(values: _*))
  }

  def apply(value: JSONValue): JSONBody = new JSONBody(value)

  // String escapement taken from scala-js
  private final val EscapeJSChars = "\\a\\b\\t\\n\\v\\f\\r\\\"\\\\"

  private def escapeJS(str: String): String = {
    // scalastyle:off return
    val end = str.length
    var i = 0
    while (i != end) {
      val c = str.charAt(i)
      if (c >= 32 && c <= 126 && c != '\\' && c != '"')
        i += 1
      else
        return createEscapeJSString(str)
    }
    str
    // scalastyle:on return
  }

  private def createEscapeJSString(str: String): String = {
    val sb = new java.lang.StringBuilder(2 * str.length)
    printEscapeJS(str, sb)
    sb.toString
  }

  private def printEscapeJS(str: String, out: java.lang.Appendable): Unit = {
    /* Note that Java and JavaScript happen to use the same encoding for
     * Unicode, namely UTF-16, which means that 1 char from Java always equals
     * 1 char in JavaScript. */
    val end = str.length()
    var i = 0
    /* Loop prints all consecutive ASCII printable characters starting
     * from current i and one non ASCII printable character (if it exists).
     * The new i is set at the end of the appended characters.
     */
    while (i != end) {
      val start = i
      var c: Int = str.charAt(i)
      // Find all consecutive ASCII printable characters from `start`
      while (i != end && c >= 32 && c <= 126 && c != 34 && c != 92) {
        i += 1
        if (i != end)
          c = str.charAt(i)
      }
      // Print ASCII printable characters from `start`
      if (start != i)
        out.append(str, start, i)

      // Print next non ASCII printable character
      if (i != end) {
        def escapeJSEncoded(c: Int): Unit = {
          if (6 < c && c < 14) {
            val i = 2 * (c - 7)
            out.append(EscapeJSChars, i, i + 2)
          } else if (c == 34) {
            out.append(EscapeJSChars, 14, 16)
          } else if (c == 92) {
            out.append(EscapeJSChars, 16, 18)
          } else {
            out.append(f"\\u$c%04x")
          }
        }
        escapeJSEncoded(c)
        i += 1
      }
    }
  }
}
