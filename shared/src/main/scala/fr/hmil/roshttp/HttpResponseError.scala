package fr.hmil.roshttp

import java.io.IOException

/** Exception thrown when an error occurs during an HTTP request.
  *
  * This exception can occur because of bad HTTP status code (status >= 400).
  *
  * This exception must be constructed via the companion object in order to factor out
  * error messages.
  */
class HttpResponseError private(
    /** The http response which triggered the error. */
    val response: HttpResponse,
    /** An message describing the error. */
    message: String)
  extends IOException(message)

object HttpResponseError {
  def badStatus(response: HttpResponse): HttpResponseError =
    new HttpResponseError(response, s"Server responded with status ${response.statusCode}")
}
