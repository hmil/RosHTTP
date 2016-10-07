package fr.hmil.roshttp.exceptions

import java.io.IOException

import fr.hmil.roshttp.response.{HttpResponse, SimpleHttpResponse, StreamHttpResponse}

/** Exception in the HTTP application layer.
  *
  * In other words, this exception occurs when a bad HTTP status code (>= 400) is received.
  */
case class HttpException[+T <: HttpResponse] private(response: T)(message: String = null)
  extends IOException(message)

object HttpException {
  def badStatus[T <: HttpResponse](response: T): HttpException[T] =
    new HttpException[T](response)(s"Server responded with status ${response.statusCode}")
}
