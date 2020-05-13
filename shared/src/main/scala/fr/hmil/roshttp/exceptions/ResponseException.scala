package fr.hmil.roshttp.exceptions

import java.io.IOException

import fr.hmil.roshttp.response.HttpResponseHeader

/** Captures network errors occurring during reception of an HTTP response body.
  *
  * When this exception occurs, HTTP headers have already been received.
  * The response header data is recovered in the header field.
  *
  * @see [[RequestException]]
  */
case class ResponseException private[roshttp](
    cause: Throwable,
    header: HttpResponseHeader)
  extends IOException("A network error occurred during HTTP response transmission.", cause)
