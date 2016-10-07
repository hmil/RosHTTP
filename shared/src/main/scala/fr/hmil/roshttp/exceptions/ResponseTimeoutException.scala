package fr.hmil.roshttp.exceptions

import fr.hmil.roshttp.response.HttpResponseHeader

/** Captures timeout exceptions occurring during an HTTP response. */
class ResponseTimeoutException(val header: Option[HttpResponseHeader] = None)
  extends TimeoutException("HTTP response timed out.")
