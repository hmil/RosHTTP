package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import fr.hmil.roshttp.CrossPlatformUtils

/** An urlencoded HTTP body.
  *
  * <b>Usage:</b> urlencoded bodies are best suited for simple key/value maps of strings. For more
  * structured data, use [[JSONBody]]. For binary data, use [[ByteBufferBody]] or [[MultiPartBody]].
  *
  * URLEncoded bodies are associated with the mime type "application/x-www-form-urlencoded"
  * and look like query string parameters (eg. key=value&key2=value2 ).
  *
  * @param values A map of key/value pairs to send with the request.
  */
class URLEncodedBody private(values: Map[String, String]) extends BulkBodyPart {

  override def contentType: String = s"application/x-www-form-urlencoded"

  override def contentData: ByteBuffer = ByteBuffer.wrap(
    values.map({case (name, part) =>
      CrossPlatformUtils.encodeURIComponent(name) +
      "=" +
      CrossPlatformUtils.encodeURIComponent(part)
    }).mkString("&").getBytes("utf-8")
  )
}

object URLEncodedBody {
  def apply(values: (String, String)*): URLEncodedBody = new URLEncodedBody(Map(values: _*))
}