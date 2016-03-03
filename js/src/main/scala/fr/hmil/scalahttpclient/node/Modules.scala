package fr.hmil.scalahttpclient.node
import fr.hmil.scalahttpclient.node.http._


/**
  * This object allows to access nodejs builtin modules without explicitely calling require.
  *
  * If a browser shim is used and is accessible in the global context, it will be returned
  * and no call to require() will take place
  */
object Modules {

  /**
    * Gets javascript module using either require() or the global context
    * @param module Module descriptor
    * @tparam T Module API facade type
    * @return The requested module as a scala facade
    */
  private def require[T](module: Module[T]): T = {
    if (!module.inst.isInstanceOf[Unit]) {
      println("Using context")
      module.inst
    }
    else {
      println("Using require")
      Global.require[T](module.name)
    }
  }

  lazy val http: Http = require(HttpModule)

}
