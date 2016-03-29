package fr.hmil.scalahttp.client

import java.net.URI
import fr.hmil.scalahttp.{Method, Protocol}
import scala.concurrent.Future

/**
  * Builds an HTTP request.
  *
  * The request is sent using  [[send]]. A request can be sent multiple times.
  * Each time yields a Future[HttpResponse] which either succeeds with an [[HttpResponse]]
  * or fails with an [[HttpException]]
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
    val parser = new URI(url)
    copy(
      protocol = if (parser.getScheme != null) parser.getScheme else protocol,
      host = if (parser.getHost != null) parser.getHost else host,
      port = if (parser.getPort != -1) parser.getPort else port,
      path = if (parser.getPath != null) parser.getPath else path
    )
  }

  def url: String = s"$protocol://$host:$port$path"

  def send(): Future[HttpResponse] = {
    HttpDriver.send(this)
  }

  private def copy(
      method: Method      = this.method,
      host: String        = this.host,
      path: String        = this.path,
      port: Int           = this.port,
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
  def apply(): HttpRequest = new HttpRequest(
    method = Method.GET,
    host = null,
    path = null,
    port = 80,
    protocol = Protocol.HTTP
  )
  def apply(url: String): HttpRequest = this() withURL(url)
}
