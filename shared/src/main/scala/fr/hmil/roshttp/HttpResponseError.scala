package fr.hmil.roshttp

import java.io.IOException

/** Exception thrown when an error occurs during an HTTP request.
  *
  * This exception can occur because of bad HTTP status code (status >= 400).
  *
  * This exception must be constructed via the companion object in order to factor out
  * error messages.
  */
abstract class HttpResponseError private(message: String) extends IOException(message)

object HttpResponseError {

  def badStatus(response: HttpResponse): HttpResponseError =
    this(response, s"Server responded with status ${response.statusCode}")

  def apply(response: HttpResponse, message: String): HttpResponseError = response match {
    case res:SimpleHttpResponse => new SimpleHttpResponseError(res, message)
    case res:StreamedHttpResponse => new StreamHttpResponseError(res, message)
  }

  case class SimpleHttpResponseError(
      /** The http response which triggered the error. */
      response: SimpleHttpResponse,
      /** An message describing the error. */
      message: String)
    extends HttpResponseError(message)
  case class StreamHttpResponseError(
      /** The http response which triggered the error. */
      response: StreamedHttpResponse,
      /** An message describing the error. */
      message: String)
    extends HttpResponseError(message)
}
