package fr.hmil.roshttp.node

import scala.scalajs.js
import js.annotation._

/**
  * Facade for objects accessible from node's global scope
  */
@js.native
@JSGlobalScope
private[roshttp] object Global extends js.Object {
  def require[T](name: String): js.UndefOr[T] = js.native
}
