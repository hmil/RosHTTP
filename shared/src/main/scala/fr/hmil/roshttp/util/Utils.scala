package fr.hmil.roshttp.util

import java.nio.ByteBuffer

import fr.hmil.roshttp.CrossPlatformUtils

object Utils {

  /**
    * Extracts the charset from a content-type header string
    * @param input content-type header value
    * @return the charset contained in the content-type or the default
    *         one-byte encoding charset (to avoid tampering binary buffer).
    */
  def charsetFromContentType(input: String): String = {
    if (input == null) {
      oneByteCharset
    } else {
      // From W3C spec:
      // Content-Type := type "/" subtype *[";" parameter]
      // eg: text/html; charset=UTF-8
      input.split(';').toStream.drop(1).foldLeft(oneByteCharset)((acc, s) => {
        if (s.matches("^\\s*charset=.+$")) {
          s.substring(s.indexOf("charset") + 8)
        } else {
          acc
        }
      })
    }
  }

  /** urlencodes a query string by preserving key-value pairs. */
  def encodeQueryString(queryString: String): String = {
    queryString
      .split("&")
      .map(_
        .split("=")
        .map(encodeURIComponent)
        .mkString("="))
      .mkString("&")
  }

  def encodeURIComponent(input: String): String = CrossPlatformUtils.encodeURIComponent(input)

  def getStringFromBuffer(byteBuffer: ByteBuffer, charset: String): String = {
    if (byteBuffer.hasArray) {
      new String(byteBuffer.array(), 0, byteBuffer.limit(), charset)
    } else {
      val tmp = new Array[Byte](byteBuffer.limit)
      byteBuffer.get(tmp)
      new String(tmp, charset)
    }
  }

  private val oneByteCharset = "utf-8"
}
