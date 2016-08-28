package fr.hmil.roshttp

private object HttpUtils {

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

  def encodeQueryString(queryString: String): String = {
    queryString
      .split("&")
      .map(_
        .split("=")
        .map(CrossPlatformUtils.encodeURIComponent)
        .mkString("="))
      .mkString("&")
  }

  val oneByteCharset = "utf-8"
}
