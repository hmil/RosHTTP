package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.reactive.Observable

import scala.concurrent.Future


class StreamedHttpResponse(
    val statusCode: Int,
    val body: Observable[ByteBuffer],
    val headers: HeaderMap[String])
  extends HttpResponse

object StreamedHttpResponse extends HttpResponseFactory[StreamedHttpResponse] {
  override def apply(
      statusCode: Int, bodyStream: Observable[ByteBuffer], headers: HeaderMap[String]): Future[StreamedHttpResponse] =
    Future.successful(new StreamedHttpResponse(statusCode, bodyStream, headers))
}
