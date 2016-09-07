package fr.hmil.roshttp.exceptions

import java.io.IOException

class UploadStreamException(message: String = null, cause: Throwable = null) extends
    IOException(UploadStreamException.defaultMessage(message, cause), cause) {
  def this(cause: Throwable) = this(null, cause)
}

object UploadStreamException {
  private def defaultMessage(message: String, cause: Throwable) =
    if (message != null) message
    else if (cause != null) cause.toString
    else null
}