package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.client.HeaderUtils.CaseInsensitiveString

/**
 * An HTTP response obtained via an [[HttpRequest]]
 */
class HttpResponse(val statusCode: Int, val body: String, val headers: Map[CaseInsensitiveString, String])