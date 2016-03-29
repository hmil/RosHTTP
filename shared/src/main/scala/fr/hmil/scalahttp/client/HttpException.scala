package fr.hmil.scalahttp.client

import java.io.IOException

/**
  * Exception thrown when an error occurs during an HTTP request.
  *
  * This exception can occur because of network issues or bad HTTP
  * status code (status >= 400).
  *
  * This exception must be constructed via the companion object in order to factor out
  * error messages.
  *
  * @param response The http response.
  *
  *   In case of network error, the response can be None
  */
class HttpException private (
    val response: Option[HttpResponse],
    message: String,
    cause: Throwable)
  extends IOException(message, cause) {

  private def this(message: String, cause: Throwable) {
    this(None, message, cause)
  }

  private def this(response: HttpResponse, message: String) {
    this(Some(response), message, null)
  }
}

object HttpException {
  def badStatus(response: HttpResponse): HttpException =
    new HttpException(response, s"Server responded with status ${response.statusCode}")

  def networkError(cause: IOException): HttpException =
    new HttpException("A network error occured", cause)
}
