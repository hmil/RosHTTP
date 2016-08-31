package fr.hmil.roshttp.exceptions

import fr.hmil.roshttp.response.SimpleHttpResponse

case class SimpleResponseTimeoutException(override val partialResponse: Option[SimpleHttpResponse])
    extends HttpTimeoutException(partialResponse)