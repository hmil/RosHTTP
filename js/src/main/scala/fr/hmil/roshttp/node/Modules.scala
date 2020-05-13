package fr.hmil.roshttp.node

import fr.hmil.roshttp.node.http.{Http, Https}

import scala.scalajs.js


/**
  * This object allows to access nodejs builtin modules without explicitely calling require.
  *
  * If a browser shim is used and is accessible in the global context, it will be returned
  * and no call to require() will take place
  */
private[roshttp] object Modules {

  object HttpModule extends Module[Http]("http") {
    override def require: Option[Http] = {
      if (js.typeOf(js.Dynamic.global.Http) != "undefined")
        Some(js.Dynamic.global.Http.asInstanceOf[Http])
      else
        Helpers.require("http")
    }
  }

  object HttpsModule extends Module[Https]("https") {
    override def require(): Option[Https] = {
      if (js.typeOf(js.Dynamic.global.Https) != "undefined")
        Some(js.Dynamic.global.Https.asInstanceOf[Https])
      else
        Helpers.require("https")
    }
  }

  lazy val http: Http = HttpModule.api
  lazy val https: Https = HttpsModule.api
}
