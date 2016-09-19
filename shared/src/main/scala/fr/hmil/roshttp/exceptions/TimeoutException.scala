package fr.hmil.roshttp.exceptions

import java.io.IOException

/** Base class for timeout exceptions. */
abstract class TimeoutException private[roshttp](message: String)
  extends IOException(message)
