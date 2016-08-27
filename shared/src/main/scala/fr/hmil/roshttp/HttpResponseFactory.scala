package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.reactive.Observable

import scala.concurrent.{ExecutionContext, Future}

private[roshttp] trait HttpResponseFactory[T <: HttpResponse] {
  def apply(statusCode: Int, bodyStream: Observable[ByteBuffer], headers: HeaderMap[String])
     (implicit ec: ExecutionContext, config: HttpConfig): Future[T]
}
