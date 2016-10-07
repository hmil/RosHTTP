package fr.hmil.roshttp.exceptions

import java.io.IOException

import fr.hmil.roshttp.response.HttpResponseHeader

/** Captures timeout exceptions occurring during an HTTP response. */
case class TimeoutException(header: Option[HttpResponseHeader] = None)
  extends IOException("HTTP response timed out.")
