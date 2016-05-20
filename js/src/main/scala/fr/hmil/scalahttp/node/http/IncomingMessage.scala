package fr.hmil.scalahttp.node.http

import fr.hmil.scalahttp.node.events.EventEmitter

import scala.collection.mutable
import scala.scalajs.js

@js.native
private[scalahttp] class IncomingMessage extends EventEmitter {
  val headers: js.Dictionary[String] = js.native
  val httpVersion: String = js.native
  val method: String = js.native
  val rawHeaders: js.Dictionary[String] = js.native
  val rawTrailers: js.Dictionary[String] = js.native
  def setTimeout(msecs: Int, callback: js.Function): IncomingMessage = js.native
  val statusCode: Int = js.native
  val statusMessage: String = js.native
  // message.socket -- not facaded here
  val trailers: js.Dictionary[String] = js.native
  val url: String = js.native
}
