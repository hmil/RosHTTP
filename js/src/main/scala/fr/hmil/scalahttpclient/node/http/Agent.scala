package fr.hmil.scalahttpclient.node.http

import scala.scalajs.js


/**
  * node http agent API.
  *
  * This facade is not complete!
  */
@js.native
class Agent extends js.Object {

  def this(options: AgentOptions) {
    this()
  }

  // def createConnection(options: net.SocketOptions): net.Socket = js.native -- Not implemented here
  // def createConnection(options: net.SocketOptions, js.Function): net.Socket = js.native -- Not implemented here

  /**
    * Destroy any sockets that are currently in use by the agent.
    *
    * It is usually not necessary to do this. However, if you are using an agent with
    * KeepAlive enabled, then it is best to explicitly shut down the agent when you
    * know that it will no longer be used. Otherwise, sockets may hang open for quite
    * a long time before the server terminates them.
    */
  def destroy(): Unit = js.native

  // val freeSockets:

  /**
    * Get a unique name for a set of request options, to determine whether a connection
    * can be reused. In the http agent, this returns host:port:localAddress.
    * In the https agent, the name includes the CA, cert, ciphers, and other
    * HTTPS/TLS-specific options that determine socket reusability.
    */
  def getName(options: RequestOptions): String = js.native


  /**
    * By default set to 256. For Agents supporting HTTP KeepAlive, this sets the
    * maximum number of sockets that will be left open in the free state.
    */
  var maxFreeSockets: Integer = js.native

  /**
    * By default set to Infinity. Determines how many concurrent sockets the agent
    * can have open per origin. Origin is either a 'host:port' or
    * 'host:port:localAddress' combination.
    */
  var maxSockets: Integer = js.native

  /**
    * An object which contains queues of requests that have not yet been assigned
    * to sockets. Do not modify.
    */
  // val requests

  /**
    * An object which contains arrays of sockets currently in use by the Agent.
    * Do not modify.
    */
  // val sockets: Seq[Socket] = js.native
}
