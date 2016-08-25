package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.reactive.Observable


class HttpStreamResponse(statusCode: Int, bodyStream: Observable[ByteBuffer], headers: HeaderMap[String])
  extends HttpResponse(statusCode, bodyStream, headers)

object HttpStreamResponse extends HttpResponseFactory[HttpStreamResponse] {
  override def apply(
      statusCode: Int, bodyStream: Observable[ByteBuffer], headers: HeaderMap[String]): HttpStreamResponse =
    new HttpStreamResponse(statusCode, bodyStream, headers)
}
