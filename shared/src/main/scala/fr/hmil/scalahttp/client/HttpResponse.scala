package fr.hmil.scalahttp.client

/**
 * An HTTP response obtained via an [[HttpRequest]]
 */
class HttpResponse(val statusCode: Int, val body: String, val headers: HeaderMap[String])