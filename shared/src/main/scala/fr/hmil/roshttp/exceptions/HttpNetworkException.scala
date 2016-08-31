package fr.hmil.roshttp.exceptions

import java.io.IOException

/** Exception thrown when an error occurs during an HTTP request.
  *
  * This exception can occur because of network issues.
  */
class HttpNetworkException(message: String = null, cause: Throwable = null) extends
  IOException(HttpNetworkException.defaultMessage(message, cause), cause) {

  def this(cause: Throwable) = this(null, cause)
}

object HttpNetworkException {
  private def defaultMessage(message: String, cause: Throwable) =
    if (message != null) message
    else if (cause != null) cause.toString
    else null
}
