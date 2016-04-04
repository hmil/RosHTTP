package fr.hmil.scalahttp.client

import java.net.{URI, URLEncoder}

import fr.hmil.scalahttp.{HttpUtils, Method, Protocol}

import scala.concurrent.Future

/** Builds an HTTP request.
  *
  * The request is sent using  [[send]]. A request can be sent multiple times.
  * Each time yields a Future[HttpResponse] which either succeeds with an [[HttpResponse]]
  * or fails with an [[HttpException]]
  *
  * TODO: document class methods
  */
final class HttpRequest  private (
    val method: Method,
    val host: String,
    val path: String,
    val port: Int,
    val protocol: Protocol,
    val queryString: Option[String]) {

  val longPath = path + queryString.map(q => s"?$q").getOrElse("")

  val queryParameters: Map[String, String] = queryString
    .map(
      _.split('&')
      .flatMap(_.split('=').toList match {
        case x :: y :: Nil => Some(x -> y)
        case _ => None
      })
      .toMap)
    .getOrElse(Map.empty[String, String])


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

  /** Creates a new HttpRequest using a query string.
    *
    * queryString is escaped before it is added to the new HttpRequest.
    * Therefore: `request.withQueryString("äéuô").queryString.get != "äéuô"`
    *
    * @param queryString The unescaped query string.
    * @return An updated version of this HttpRequest
    */
  def withQueryString(queryString: String): HttpRequest =
    copy(queryString = Some(CrossPlatformUtils.encodeQueryString(queryString)))

  /** Adds a raw query string to the request (the escaped query string as it is sent over the network).
    *
    * Raw query strings must only contain legal characters as per rfc3986. Adding
    * special characters yields undefined behaviour.
    *
    * @param queryString The raw, escaped query string
    * @return An updated version of this HttpRequest
    * @see withQueryString
    */
  def withRawQueryString(queryString: String): HttpRequest =
    copy(queryString = Some(queryString))

  def withoutQuery(): HttpRequest =
    copy(queryString = None)

  def withQueryParameter(key: String, value: String): HttpRequest =
    withQueryParameters(Map(key -> value))

  def withQueryParameter(key: String, value: List[String]): HttpRequest =
    withQueryParameter(key, (value.indices.map(_.toString) zip value).toMap)

  def withQueryParameter(key: String, value: Map[String, String]): HttpRequest =
  withQueryParameters(value.map(p => (s"$key[${p._1}]", p._2)))

  def withQueryParameters(parameters: Map[String, String]): HttpRequest = {
    withRawQueryString((queryParameters ++ parameters)
      .foldRight(List.empty[String])((e, acc) =>
        (CrossPlatformUtils.encodeQueryString(e._1) +
          "=" +
          CrossPlatformUtils.encodeQueryString(e._2)) :: acc)
      .mkString("&"))
  }

  def withURL(url: String): HttpRequest = {
    val parser = new URI(url)
    copy(
      protocol = if (parser.getScheme != null) parser.getScheme else protocol,
      host = if (parser.getHost != null) parser.getHost else host,
      port = if (parser.getPort != -1) parser.getPort else port,
      path = if (parser.getPath != null) parser.getPath else path,
      queryString =
        if (parser.getQuery != null)
          Some(CrossPlatformUtils.encodeQueryString(parser.getQuery))
        else
          queryString
    )
  }

  def url: String = s"$protocol://$host:$port$longPath"

  def send(): Future[HttpResponse] = HttpDriver.send(this)

  private def copy(
      method: Method      = this.method,
      host: String        = this.host,
      path: String        = this.path,
      port: Int           = this.port,
      protocol: Protocol  = this.protocol,
      queryString: Option[String] = this.queryString
  ): HttpRequest = {
    new HttpRequest(
      method    = method,
      host      = host,
      path      = path,
      port      = port,
      protocol  = protocol,
      queryString = queryString)
  }

}

object HttpRequest {
  def apply(): HttpRequest = new HttpRequest(
    method = Method.GET,
    host = null,
    path = null,
    port = 80,
    protocol = Protocol.HTTP,
    queryString = None
  )
  def apply(url: String): HttpRequest = this().withURL(url)
}
