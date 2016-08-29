package fr.hmil.roshttp.exceptions

import fr.hmil.roshttp.response.HttpResponse

case class HttpTimeoutException(partialResponse: Option[HttpResponse]) extends
  HttpNetworkException("Request timed out")
