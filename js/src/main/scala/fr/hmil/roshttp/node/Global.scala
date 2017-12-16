package fr.hmil.roshttp.node

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

/**
  * Facade for objects accessible from node's global scope
  */
@js.native
private[roshttp] object Global extends js.GlobalScope {
  def require[T](name: String): js.UndefOr[T] = js.native
}
