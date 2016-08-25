package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.reactive.Observable

private[roshttp] trait HttpResponseFactory[T <: HttpResponse] {
  def apply(statusCode: Int, bodyStream: Observable[ByteBuffer], headers: HeaderMap[String]): T
}
