package fr.hmil.roshttp.node.events

import scala.scalajs.js

@js.native
private[roshttp] trait EventEmitter extends js.Object {
  def on(event: String, cb: js.Function1[js.Dynamic, Unit]): Unit = js.native
}
