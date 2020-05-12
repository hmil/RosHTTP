package fr.hmil.roshttp.response

import fr.hmil.roshttp.util.HeaderMap

/** Data contained by the header section of an HTTP response message
  *
  * Most of the time, [[SimpleHttpResponse]] and [[StreamHttpResponse]] are the classes
  * you will want to use since they contain both header and body data.
  * However, if a network error occurs and the response body cannot be retreived, this class
  * is used to represent the header data received.
  */
class HttpResponseHeader(val statusCode: Int, val headers: HeaderMap[String])
