package fr.hmil.roshttp


case class HttpTimeoutException(partialResponse: Option[HttpResponse]) extends
  HttpNetworkException("Request timed out")
