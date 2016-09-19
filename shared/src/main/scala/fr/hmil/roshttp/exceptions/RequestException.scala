package fr.hmil.roshttp.exceptions

import java.io.IOException

/** Captures network errors occurring during an HTTP request.
  *
  * @see [[ResponseException]]
  */
class RequestException(cause: Throwable)
  extends IOException("A network error occurred during HTTP request transmission.", cause)