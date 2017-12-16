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
  def isRequireAvailable: Boolean = !js.isUndefined(js.Dynamic.global.selectDynamic("require"))

  /**
    * Gets javascript module using either require() or the global context
    *
    * @param module Module descriptor
    * @tparam T Module API facade type
    * @return The requested module as a scala facade
    */
  def require[T](module: Module[T]): Option[T] = {
    if (!js.isUndefined(module.inst)) {
      Some(module.inst)
    } else if (isRequireAvailable) {
      try {
        Global.require[T](module.name).toOption
      } catch {
        case _: JavaScriptException => None
      }
    } else {
      None
    }
  }
}
