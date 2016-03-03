package fr.hmil.scalahttpclient.node.http

import scala.collection.mutable.{Map, Seq}
import scala.scalajs.js

@js.native
class IncomingMessage extends js.Object {
  val headers: Map[String, String] = js.native
  val httpVersion: String = js.native
  val method: String = js.native
  val rawHeaders: Seq[String] = js.native
  val rawTrailers: Seq[String] = js.native
  def setTimeout(msecs: Int, callback: js.Function): IncomingMessage = js.native
  val statusCode: Number = js.native
  val statusMessage: String = js.native
  // message.socket -- not facaded here
  val trailers: Map[String, String] = js.native
  val url: String = js.native
}
