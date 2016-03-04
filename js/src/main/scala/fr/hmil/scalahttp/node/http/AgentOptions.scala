package fr.hmil.scalahttp.node.http

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined


@ScalaJSDefined
trait AgentOptions extends js.Object {
  val keepAlive: Boolean
  val keepAliveMsecs: Integer
  val maxSockets: Integer
  val maxFreeSockets: Integer
}


object AgentOptions {

  /**
    *
    * @param keepAlive Keep sockets around in a pool to be used by other requests in the future. Default = false
    * @param keepAliveMsecs When using HTTP KeepAlive, how often to send TCP KeepAlive packets over
    *                       sockets being kept alive. Default = 1000. Only relevant if keepAlive is set to true.
    * @param maxSockets Maximum number of sockets to allow per host. Default = Infinity.
    * @param maxFreeSockets Maximum number of sockets to leave open in a free state. Only relevant
    *                       if keepAlive is set to true. Default = 256.
    * @return An AgentOption instance
    */
  def apply(
    keepAlive: js.UndefOr[Boolean] = js.undefined,
    keepAliveMsecs: js.UndefOr[Integer] = js.undefined,
    maxSockets: js.UndefOr[Integer] = js.undefined,
    maxFreeSockets: js.UndefOr[Integer] = js.undefined

  ): AgentOptions = {
    val r = js.Dynamic.literal()

    keepAlive.foreach(r.keepAlive = _)
    keepAliveMsecs.foreach(r.keepAliveMsecs = _)
    maxSockets.foreach(r.maxSockets = _)
    maxFreeSockets.foreach(r.maxFreeSockets = _)

    r.asInstanceOf[AgentOptions]
  }
}
