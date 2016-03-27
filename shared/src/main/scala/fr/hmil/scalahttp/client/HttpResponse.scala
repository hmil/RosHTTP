package fr.hmil.scalahttp.client

import java.net.URL

import scala.concurrent.Future

class HttpResponse(val statusCode: Int, val body: String)
