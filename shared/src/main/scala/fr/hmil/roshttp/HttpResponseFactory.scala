package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.reactive.Observable

import scala.concurrent.Future

private[roshttp] trait HttpResponseFactory[T <: HttpResponse] {
  def apply(statusCode: Int, bodyStream: Observable[ByteBuffer], headers: HeaderMap[String]): Future[T]
}
