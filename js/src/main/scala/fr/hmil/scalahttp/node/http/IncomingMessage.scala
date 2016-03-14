package fr.hmil.scalahttp.node.http

import scala.collection.mutable
import scala.scalajs.js

@js.native
class IncomingMessage extends js.Object {
  val headers: mutable.Map[String, String] = js.native
  val httpVersion: String = js.native
  val method: String = js.native
  val rawHeaders: mutable.Seq[String] = js.native
  val rawTrailers: mutable.Seq[String] = js.native
  def setTimeout(msecs: Int, callback: js.Function): IncomingMessage = js.native
  val statusCode: Int = js.native
  val statusMessage: String = js.native
  // message.socket -- not facaded here
  val trailers: mutable.Map[String, String] = js.native
  val url: String = js.native

  def on(event: String, cb: js.Function1[js.Dynamic, Unit]): Unit = js.native

  /*def on[T](event: Event[T], cb: (T) => Unit): Unit = {
    on(event.name, (obj: js.Dynamic) =>
      cb(obj.asInstanceOf[T])
    )
  }*/
}

//class Event[T](val name: String)

//object DataEvent extends Event[String]("data")