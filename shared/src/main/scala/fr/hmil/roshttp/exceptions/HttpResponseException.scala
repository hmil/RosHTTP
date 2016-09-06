package fr.hmil.roshttp.exceptions

import java.io.IOException

import fr.hmil.roshttp.response.{HttpResponse, SimpleHttpResponse, StreamHttpResponse}

/** Exception thrown when an error occurs during an HTTP request.
  *
  * This exception can occur because of bad HTTP status code (status >= 400).
  *
  * This exception must be constructed via the companion object in order to factor out
  * error messages.
  */
abstract class HttpResponseException private(message: String) extends IOException(message)

object HttpResponseException {

  def badStatus(response: HttpResponse): HttpResponseException =
    this(response, s"Server responded with status ${response.statusCode}")

  def apply(response: HttpResponse, message: String): HttpResponseException = response match {
    case res:SimpleHttpResponse => SimpleHttpResponseException(res, message)
    case res:StreamHttpResponse => StreamHttpResponseException(res, message)
  }

  case class SimpleHttpResponseException(
      /** The http response which triggered the error. */
      response: SimpleHttpResponse,
      /** An message describing the error. */
      message: String)
    extends HttpResponseException(message)
  case class StreamHttpResponseException(
      /** The http response which triggered the error. */
      response: StreamHttpResponse,
      /** An message describing the error. */
      message: String)
    extends HttpResponseException(message)
}
