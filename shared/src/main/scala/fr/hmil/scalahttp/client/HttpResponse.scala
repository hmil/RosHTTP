package fr.hmil.scalahttp.client

import java.net.URL

import scala.concurrent.Future

/**
 * A successful HTTP response.
 *
 * This only contains the status code and the response body.
 *
 * Response headers will be implemented soon.
 */
class HttpResponse(val statusCode: Int, val body: String)
