package fr.hmil.scalahttp.events

import scala.scalajs.js

@js.native
trait EventEmitter extends js.Object {
  def on(event: String, cb: js.Function1[js.Dynamic, Unit]): Unit = js.native
}
