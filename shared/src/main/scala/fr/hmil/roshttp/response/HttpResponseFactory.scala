package fr.hmil.roshttp.response

import java.nio.ByteBuffer

import fr.hmil.roshttp.BackendConfig
import fr.hmil.roshttp.util.HeaderMap
import monix.execution.Scheduler
import monix.reactive.Observable

import scala.concurrent.Future

private[roshttp] trait HttpResponseFactory[T <: HttpResponse] {
  def apply(
    header: HttpResponseHeader,
    bodyStream: Observable[ByteBuffer],
    config: BackendConfig)
    (implicit scheduler: Scheduler): Future[T]
}
