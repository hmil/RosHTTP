package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.reactive.Observable


private[roshttp] abstract class HttpResponse(
    val statusCode: Int,
    val bodyStream: Observable[ByteBuffer],
    val headers: HeaderMap[String])