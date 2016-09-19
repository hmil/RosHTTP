package fr.hmil.roshttp.exceptions

/** Captures timeout exceptions occurring during an HTTP request. */
class RequestTimeoutException private[roshttp]()
  extends TimeoutException("HTTP request timed out.")
