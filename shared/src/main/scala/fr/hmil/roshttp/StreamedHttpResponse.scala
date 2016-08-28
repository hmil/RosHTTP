package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.concurrent.Scheduler
import monifu.reactive.Observable

import scala.concurrent.Future


class StreamedHttpResponse(
    val statusCode: Int,
    val headers: HeaderMap[String],
    val body: Observable[ByteBuffer])
extends HttpResponse

object StreamedHttpResponse extends HttpResponseFactory[StreamedHttpResponse] {
  override def apply(
      statusCode: Int,
      headers: HeaderMap[String],
      bodyStream: Observable[ByteBuffer],
      config: BackendConfig)
      (implicit scheduler: Scheduler): Future[StreamedHttpResponse] =
    Future.successful(new StreamedHttpResponse(statusCode, headers, bodyStream))
}