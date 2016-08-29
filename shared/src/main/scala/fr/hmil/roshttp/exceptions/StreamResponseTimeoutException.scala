package fr.hmil.roshttp.exceptions

import fr.hmil.roshttp.response.StreamHttpResponse

case class StreamResponseTimeoutException(override val partialResponse: Option[StreamHttpResponse])
  extends HttpTimeoutException(partialResponse)
