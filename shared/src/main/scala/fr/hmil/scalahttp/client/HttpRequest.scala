package fr.hmil.scalahttp.client

import java.net.{URI, URLEncoder}

import fr.hmil.scalahttp.{HttpUtils, Method, Protocol}

import scala.concurrent.Future

/** Builds an HTTP request.
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
    val protocol: Protocol,
    val queryString: Option[String]) {

  /** The path with the query string or just the path if there is no query string */
  val longPath = path + queryString.map(q => s"?$q").getOrElse("")

  /** A map of query parameters present in the query string */
  val queryParameters: Map[String, String] =
    queryString
      .map(
        _.split('&')
        .flatMap(_.split('=').toList match {
          case x :: y :: Nil =>
            Some(CrossPlatformUtils.decodeQueryString(x) -> CrossPlatformUtils.decodeQueryString(y))
          case _ => None
        })
        .toMap)
      .getOrElse(Map.empty[String, String])

  /** The target url for this request */
  val url: String = s"$protocol://$host:$port$longPath"

/*
  def withMethod(method: Method): HttpRequest =
    copy(method = method)
*/

  /** Sets the host used in the request URI.
    *
    * @param host The new host
    * @return A copy of this [[HttpRequest]] with an updated host
    */
  def withHost(host: String): HttpRequest =
    copy(host = host)

  /** Sets the path used in the request URI.
    *
    * The path is the part that lies between the host (or port if present)
    * and the query string or the end of the request. Note that the query
    * string is not part of the path.
    *
    * @param path The new path, including the leading '/' and excluding
    *             any '?' or '#' and subsequent characters
    * @return A copy of this [[HttpRequest]] with an updated path
    */
  def withPath(path: String): HttpRequest =
    copy(path = path)

  /** Sets the port used in the request URI.
    *
    * @param port The new port
    * @return A copy of this [[HttpRequest]] with an updated port
    */
  def withPort(port: Int): HttpRequest =
    copy(port = port)

  /** Sets the protocol used in the request URL.
    *
    * At the moment, only HTTP is supported.
    *
    * @throws IllegalArgumentException when something else than HTTP is passed as argument
    * @param protocol The HTTP protocol
    * @return A copy of this [[HttpRequest]] with an updated protocol
    */
  def withProtocol(protocol: Protocol): HttpRequest = {
    if (protocol != Protocol.HTTP) {
      throw new IllegalArgumentException(s"HttpRequest only supports the HTTP protocol. $protocol was provided")
    }
    copy(protocol = protocol)
  }

  /** Sets the query string.
    *
    * The argument is escaped by this method. If you want to bypass the escaping, use [[withQueryStringRaw]].
    *
    * Escaping also means that the queryString property will generally not be equal to what you passed as
    * argument to [[withQueryString]].
    *
    * For instance: `request.withQueryString("äéuô").queryString.get != "%C3%A4%C3%A9u%C3%B4"`
    *
    * @param queryString The unescaped query string.
    * @return A copy of this [[HttpRequest]] with an updated queryString
    */
  def withQueryString(queryString: String): HttpRequest =
    copy(queryString = Some(CrossPlatformUtils.encodeQueryString(queryString)))

  /** Sets the query string without escaping.
    *
    * Raw query strings must only contain legal characters as per rfc3986. Adding
    * special characters yields undefined behaviour.
    *
    * In most cases, [[withQueryString]] should be preferred.
    *
    * @param queryString The raw, escaped query string
    * @return A copy of this [[HttpRequest]] with an updated queryString
    */
  def withQueryStringRaw(queryString: String): HttpRequest =
    copy(queryString = Some(queryString))

  /** Removes the query string.
    *
    * @return A copy of this [[HttpRequest]] without query string
    */
  def withoutQuery(): HttpRequest =
    copy(queryString = None)

  /** Adds a query parameter or updates it if it already exists.
    *
    * Query parameters end up in the query string as `key=value` pairs separated by ampersands.
    * Both the key and parameter are escaped to ensure proper query string format.
    *
    * @param key The unescaped parameter key
    * @param value The unescaped parameter value
    * @return A copy of this [[HttpRequest]] with an updated query string.
    */
  def withQueryParameter(key: String, value: String): HttpRequest =
    withQueryParameters(Map(key -> value))

  /** Adds a query array parameter or updates it if it already exists.
    *
    * Although this is not part of a spec, most servers recognize bracket indices
    * in a query string as array indices or object keys.
    *
    * example: ?list[0]=foo&list[1]=bar
    *
    * This method formats array values according to the above example.
    *
    * @param key The unescaped parameter key
    * @param values The unescaped parameter array values
    * @return A copy of this [[HttpRequest]] with an updated query string.
    * @see [[withQueryParameter(String,String)]]
    */
  def withQueryParameter(key: String, values: List[String]): HttpRequest =
    withQueryParameter(key, (values.indices.map(_.toString) zip values).toMap)

  /** Adds a query map parameter or updates it if it already exists.
    *
    * Although this is not part of a spec, most servers recognize bracket indices
    * in a query string as array indices or object keys.
    *
    * example: ?obj[foo]=bar&obj[baz]=42
    *
    * This method formats map values according to the above example.
    *
    * @param key The unescaped parameter key
    * @param values The unescaped parameter map values
    * @return A copy of this [[HttpRequest]] with an updated query string.
    * @see [[withQueryParameter(String,String)]]
    */
  def withQueryParameter(key: String, values: Map[String, String]): HttpRequest =
    withQueryParameters(values.map(p => (s"$key[${p._1}]", p._2)))

  /** Adds multiple query parameters and updates those already existing.
    *
    * @param parameters A map of new parameters.
    * @return A copy of this [[HttpRequest]] with an updated query string.
    * @see [[withQueryParameter(String,String)]]
    */
  def withQueryParameters(parameters: Map[String, String]): HttpRequest =
    setQueryParameters(queryParameters ++ parameters.map(p => (p._1, p._2)))

  /** Removes one query parameter from the query string.
    *
    * @param key The unescaped parameter key to remove
    * @return A copy of this [[HttpRequest]] with an updated query string.
    * @see [[withQueryParameter(String,String)]]
    */
  def withoutQueryParameter(key: String): HttpRequest =
    setQueryParameters(queryParameters - key)

  /** Updates request protocol, host, port, path and queryString according to a url.
    *
    * @param url A valid HTTP url
    * @return A copy of this [[HttpRequest]] with updated URL-related attributes.
    */
  def withURL(url: String): HttpRequest = {
    val parser = new URI(url)
    copy(
      protocol = if (parser.getScheme != null) parser.getScheme else protocol,
      host = if (parser.getHost != null) parser.getHost else host,
      port = if (parser.getPort != -1) parser.getPort else port,
      path = if (parser.getPath != null) parser.getPath else  path,
      queryString =
        if (parser.getQuery != null)
          Some(CrossPlatformUtils.encodeQueryString(parser.getQuery))
        else
          queryString
    )
  }

  /** Sends this request.
    *
    * A request can be sent multiple times. When a request is sent, it returns a Future[HttpResponse]
    * which either succeeds with an [[HttpResponse]] or fails with an [[HttpException]].
    *
    * Possible reasons for the future failing are:
    * - A status code >= 400
    * - A network error
    *
    * Note that in some cases the response body can still be obtained after a failure
    * through the [[HttpException]].
    *
    * @return A future of HttpResponse which may fail with an [[HttpException]]
    */
  def send(): Future[HttpResponse] = HttpDriver.send(this)


  /** Internal method to replace all query parameters at once */
  private def setQueryParameters(parameters: Map[String, String]): HttpRequest = {
    if (parameters.isEmpty) {
      withoutQuery()
    } else {
      withQueryStringRaw(parameters
        .foldRight(List.empty[String])((e, acc) => {
          CrossPlatformUtils.encodeQueryString(e._1) + "=" + CrossPlatformUtils.encodeQueryString(e._2) ::
            acc
        })
        .mkString("&"))
    }
  }

  /** Internal method to back public facing .withXXX methods. */
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

  private val default = new HttpRequest(
    method = Method.GET,
    host = null,
    path = null,
    port = 80,
    protocol = Protocol.HTTP,
    queryString = None
  )

  /** Creates a blank HTTP request.
    *
    * It is most of the time more convenient to use apply(String) but this
    * constructor allows you to programatically construct a request from scratch.
    *
    * @return [[HttpRequest.default]]
    */
  def apply(): HttpRequest = default

  /** Creates an [[HttpRequest]] with the provided target url.
    *
    * Same thing as calling `HttpRequest().withURL(url)`.
    *
    * @param url The target url.
    * @return An [[HttpRequest]] ready to GET the target url.
    */
  def apply(url: String): HttpRequest = this().withURL(url)
}
