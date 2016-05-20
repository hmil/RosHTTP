package fr.hmil.scalahttp.node

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

/**
  * Facade for objects accessible from node's global scope
  */
@js.native
@JSName("global")
private[scalahttp] object Global extends js.GlobalScope {
  def require[T](name: String): T = js.native
}
