package fr.hmil.roshttp.exceptions

import java.io.IOException

/** Captures errors in the request body stream.
  *
  * This exception means that the stream which feeds request body data into the request broke.
  */
case class UploadStreamException(cause: Throwable)
  extends IOException("An error occurred upstream while sending request data.", cause)