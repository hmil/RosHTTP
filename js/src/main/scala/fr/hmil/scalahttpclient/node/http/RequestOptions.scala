package fr.hmil.scalahttpclient.node.http


import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined



@ScalaJSDefined
trait RequestOptions extends js.Object {
  val protocol: String
  val host: String
  val hostname: String
  val family: Int
  val port: Int
  val localAddress: String
  val socketPath: String
  val method: String
  val path: String
  val headers: Map[String, String]
  val auth: String
  val agent: Agent
  // val createConnection -- TODO
}

object RequestOptions {

  /**
    * @param protocol Protocol to use. Defaults to 'http:'.
    * @param host A domain name or IP address of the server to issue the request to. Defaults to 'localhost'.
    * @param hostname Alias for host. To support url.parse() hostname is preferred over host.
    * @param family IP address family to use when resolving host and hostname. Valid values are 4 or 6.
    *               When unspecified, both IP v4 and v6 will be used.
    * @param port Port of remote server. Defaults to 80.
    * @param localAddress Local interface to bind for network connections.
    * @param socketPath Unix Domain Socket (use one of host:port or socketPath).
    * @param method A string specifying the HTTP request method. Defaults to 'GET'.
    * @param path Request path. Defaults to '/'. Should include query string if any. E.G. '/index.html?page=12'.
    *             An exception is thrown when the request path contains illegal characters. Currently, only
    *             spaces are rejected but that may change in the future.
    * @param headers An object containing request headers.
    * @param auth Basic authentication i.e. 'user:password' to compute an Authorization header.
    * @param agent Controls Agent behavior. When an Agent is used request will default to Connection: keep-alive.
    * @return
    */
  def apply(
    protocol: js.UndefOr[String] = js.undefined,
    host: js.UndefOr[String] = js.undefined,
    hostname: js.UndefOr[String] = js.undefined,
    family: js.UndefOr[Int] = js.undefined,
    port: js.UndefOr[Int] = js.undefined,
    localAddress: js.UndefOr[String] = js.undefined,
    socketPath: js.UndefOr[String] = js.undefined,
    method: js.UndefOr[String] = js.undefined,
    path: js.UndefOr[String] = js.undefined,
    headers: js.UndefOr[Map[String, String]] = js.undefined,
    auth: js.UndefOr[String] = js.undefined,
    agent: js.UndefOr[Agent] = js.undefined

  ): RequestOptions = {
    val r = js.Dynamic.literal()

    protocol.foreach(r.protocol = _)
    host.foreach(r.host = _)
    hostname.foreach(r.hostname = _)
    family.foreach(r.family = _)
    port.foreach(r.port = _)
    localAddress.foreach(r.localAddress = _)
    socketPath.foreach(r.socketPath = _)
    method.foreach(r.method = _)
    path.foreach(r.path = _)
    headers.foreach(r.headers = _)
    auth.foreach(r.auth = _)
    agent.foreach(r.agent = _)

    r.asInstanceOf[RequestOptions]
  }
}

