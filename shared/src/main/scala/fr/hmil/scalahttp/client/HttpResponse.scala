package fr.hmil.scalahttp.client

/**
 * A successful HTTP response.
 *
 * This only contains the status code and the response body.
 *
 * Response headers will be implemented soon.
 */
class HttpResponse(val statusCode: Int, val body: String)
