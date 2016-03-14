package fr.hmil.scalahttp.client

import scala.concurrent.Future

class HttpResponse(val statusCode: Int, val body: String)
