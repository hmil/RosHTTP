package fr.hmil.scalahttp.node.http

import fr.hmil.scalahttp.events.EventEmitter

import scala.collection.mutable
import scala.scalajs.js

@js.native
class IncomingMessage extends EventEmitter {
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
}
