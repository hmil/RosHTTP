package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.reactive.Observable


private[roshttp] trait HttpResponse {
    val statusCode: Int
    val body: Any
    val headers: HeaderMap[String]
}