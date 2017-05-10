package fr.hmil.roshttp.node.http

import fr.hmil.roshttp.node.events.EventEmitter

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
private[roshttp] class IncomingMessage extends EventEmitter {
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

  def pause(): Unit = js.native
  def resume(): Unit = js.native
}
