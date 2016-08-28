package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.concurrent.Scheduler
import monifu.reactive.Observable

import scala.concurrent.Future

private[roshttp] trait HttpResponseFactory[T <: HttpResponse] {
  def apply(
    statusCode: Int,
    headers: HeaderMap[String],
    bodyStream: Observable[ByteBuffer],
    config: BackendConfig)
    (implicit scheduler: Scheduler): Future[T]
}
