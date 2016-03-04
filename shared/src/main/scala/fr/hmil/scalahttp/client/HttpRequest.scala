package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.{Protocol, Method}
import scala.concurrent.Future

/**
  * TODO: doc
  */
final class HttpRequest  private (
    val method: Method,
    val host: String,
    val path: String,
    val port: Int,
    val protocol: Protocol) {


  def withMethod(method: Method): HttpRequest =
    copy(method = method)

  def withHost(host: String): HttpRequest =
    copy(host = host)

  def withPath(path: String): HttpRequest =
  copy(path = path)

  def withPort(port: Int): HttpRequest =
    copy(port = port)

  def withProtocol(protocol: Protocol): HttpRequest =
    copy(protocol = protocol)

  def withURL(url: String): HttpRequest = {
    // TODO: parse url into components
    copy()
  }

  def send: Future[HttpResponse] = {
    HttpDriver.send(this)
  }

  private def copy(
      method: Method      = this.method,
      host: String        = this.host,
      path: String        = this.path,
      port: Int   = this.port,
      protocol: Protocol  = this.protocol
  ): HttpRequest = {
    new HttpRequest(
      method    = method,
      host      = host,
      path      = path,
      port      = port,
      protocol  = protocol)
  }

}

object HttpRequest {
  def create: HttpRequest = new HttpRequest(
    method = Method.GET,
    host = null,
    path = null,
    port = 80,
    protocol = Protocol.HTTP
  )
}
