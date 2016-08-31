package fr.hmil.roshttp.exceptions

import fr.hmil.roshttp.response.HttpResponse

abstract class HttpTimeoutException(val partialResponse: Option[HttpResponse]) extends
  HttpNetworkException("Request timed out") {

  def unapply(arg: HttpTimeoutException): Option[Option[HttpResponse]] = Some(arg.partialResponse)
}
