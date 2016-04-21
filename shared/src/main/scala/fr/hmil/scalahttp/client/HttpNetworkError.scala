package fr.hmil.scalahttp.client

import java.io.IOException

/** Exception thrown when an error occurs during an HTTP request.
  *
  * This exception can occur because of network issues.
  *
  * @param response The http response.
  */
class HttpNetworkError(cause: Throwable)
  extends IOException(cause)
