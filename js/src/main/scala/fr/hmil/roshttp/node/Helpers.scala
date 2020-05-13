package fr.hmil.roshttp.node

import scala.scalajs.js
import scala.scalajs.js.JavaScriptException

/**
  * collection of helper functions for nodejs related stuff
  */
private[roshttp] object Helpers {

  /**
    * Tests whether the current environment is commonjs-like
 *
    * @return true if the function "require" is available on the global scope
    */
  def isRequireAvailable: Boolean = js.typeOf(js.Dynamic.global.selectDynamic("require")) != "undefined"

  /**
    * Gets javascript module using require()
    *
    * @param moduleName Module name
    * @return The requested module as a scala facade
    */
  def require[T](moduleName: String): Option[T] = {
    if (isRequireAvailable) {
      try {
        Global.require[T](moduleName).toOption
      } catch {
        case _: JavaScriptException => None
      }
    } else {
      None
    }
  }
}
