package fr.hmil.roshttp

import java.net.URI
import java.util.concurrent.TimeUnit

import fr.hmil.roshttp.body.BodyPart
import fr.hmil.roshttp.exceptions.TimeoutException
import fr.hmil.roshttp.response.{HttpResponse, HttpResponseFactory, SimpleHttpResponse, StreamHttpResponse}
import fr.hmil.roshttp.util.{HeaderMap, Utils}
import monix.execution.Scheduler

import scala.concurrent.{Future, Promise}
import scala.concurrent.duration.{FiniteDuration, TimeUnit}
import scala.util.{Success, Failure}

/** Builds an HTTP request.
  *
  * The request is sent using  [[send]]. A request can be sent multiple times.
  */
final class HttpRequest  private (
    val method: Method,
    val host: String,
    val path: String,
    val port: Option[Int],
    val protocol: Protocol,
    val queryString: Option[String],
    val credentials: Boolean,
    val headers: HeaderMap[String],
    val body: Option[BodyPart],
    val backendConfig: BackendConfig,
    val timeout: FiniteDuration) {

  /** The path with the query string or just the path if there is no query string */
  val longPath = path + queryString.map(q => s"?$q").getOrElse("")

  /** The target url for this request */
  val url: String = s"$protocol://$host${port.fold("")(":" + _)}$longPath"

  /** Sets the HTTP method. Defaults to GET.
    *
    * Beware of browser limitations when using exotic methods.
    *
    * @param method The new method
    * @return A copy of this [[HttpRequest]] with an updated method
    */
  def withMethod(method: Method): HttpRequest =
    copy(method = method)

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
    copy(port = Some(port))

  /** Discards changes introduced by any call to [[withPort]]
    *
    * @return A copy of this [[HttpRequest]] with no explicit port.
    */
  def withDefaultPort(): HttpRequest =
    copy(port = None)

  /** Sets the protocol used in the request URL.
    *
    * Setting the protocol also sets the port accordingly (80 for HTTP, 443 for HTTPS).
    *
    * @param protocol The HTTP or HTTPS protocol
    * @return A copy of this [[HttpRequest]] with an updated protocol and port
    */
  def withProtocol(protocol: Protocol): HttpRequest = {
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
    copy(queryString = Some(Utils.encodeQueryString(queryString)))

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
  def withoutQueryString(): HttpRequest =
    copy(queryString = None)

  /** Adds a query parameter key/value pair.
    *
    * Query parameters end up in the query string as `key=value` pairs separated by ampersands.
    * Both the key and parameter are escaped to ensure proper query string format.
    *
    * @param key The unescaped parameter key
    * @param value The unescaped parameter value
    * @return A copy of this [[HttpRequest]] with an updated query string.
    */
  def withQueryParameter(key: String, value: String): HttpRequest =
    copy(queryString = Some(
      queryString.map(q => q + '&').getOrElse("") +
      CrossPlatformUtils.encodeURIComponent(key) +
      "=" +
      CrossPlatformUtils.encodeURIComponent(value)))

  /** Adds a query array parameter.
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
    */
  def withQuerySeqParameter(key: String, values: Seq[String]): HttpRequest =
    values.foldLeft(this)((acc, value) => acc.withQueryParameter(key, value))

  /** Adds an object made of key/value pairs to the query string.
    *
    * Although this is not part of a spec, most servers recognize bracket indices
    * in a query string as object keys. The same key can appear multiple times as
    * some server will interpret this as multiple elements in an array for that key.
    *
    * example: ?obj[foo]=bar&obj[baz]=42
    *
    * @param key The unescaped parameter key
    * @param values The unescaped parameter map values
    * @return A copy of this [[HttpRequest]] with an updated query string.
    */
  def withQueryObjectParameter(key: String, values: Seq[(String, String)]): HttpRequest =
    withQueryParameters(values.map(p => (s"$key[${p._1}]", p._2)): _*)

  /** Adds multiple query parameters.
    *
    * @param parameters A sequence of new, unescaped parameters.
    * @return A copy of this [[HttpRequest]] with an updated query string.
    */
  def withQueryParameters(parameters: (String, String)*): HttpRequest =
    parameters.foldLeft(this)((acc, entry) => acc.withQueryParameter(entry._1, entry._2))

  /** Adds or updates a header to the current set of headers.
    *
    * @param key The header key (case insensitive)
    * @param value The header value
    * @return A copy of this [[HttpRequest]] with an updated header set.
    */
  def withHeader(key: String, value: String): HttpRequest =
    copy(headers = HeaderMap(headers + (key -> value)))

  /** Adds or updates multiple headers to the current set of headers.
    *
    * @param newHeaders The headers to add.
    * @return A copy of this [[HttpRequest]] with an updated header set.
    */
  def withHeaders(newHeaders: (String, String)*): HttpRequest =
    copy(headers = HeaderMap(headers ++ newHeaders))

  def withCredentials(toggle: Boolean) =
    copy(credentials = toggle)
    
  /** Specifies the request timeout.
    *
    * When a request takes longer than timeout to complete, the future is
    * rejected with a [[fr.hmil.roshttp.exceptions.TimeoutException]].
    *
    * @param timeout The duration to wait before throwing a timeout exception.
    * @return A copu of this [[HttpRequest]] with an updated timeout setting.
    */
  def withTimeout(timeout: FiniteDuration): HttpRequest =
    copy(timeout = timeout)

  /** Updates request protocol, host, port, path and queryString according to a url.
    *
    * @param url A valid HTTP url
    * @return A copy of this [[HttpRequest]] with updated URL-related attributes.
    */
  def withURL(url: String): HttpRequest = {
    val parser = new URI(url)
    copy(
    protocol = if (parser.getScheme != null) Protocol.fromString(parser.getScheme) else protocol,
    host = if (parser.getHost != null) parser.getHost else host,
    port = if (parser.getPort != -1) Some(parser.getPort) else port,
    path = if (parser.getPath != null) parser.getPath else  path,
    queryString = {
        if (parser.getQuery != null)
        Some(Utils.encodeQueryString(parser.getQuery))
        else
        queryString
      }
    )
  }

  /**
    * Use the provided backend configuration when executing the request
    */
  def withBackendConfig(backendConfig: BackendConfig): HttpRequest = {
    copy(backendConfig = backendConfig)
  }

  /** Attaches a body to this request and sets the Content-Type header.
    *
    * The body will be sent with the request regardless of other parameters once
    * [[send()]] is invoked. Any subsequent call to [[withBody()]], [[send(BodyPart)]],
    * [[post(BodyPart)]], [[put(BodyPart)]] or similar methods will override the request body.
    *
    * The Content-Type header is set to this body's content-type. It can still be manually
    * overridden using a method of the [[withHeader()]] family.
    *
    * Note that the HTTP spec forbids sending data with some methods. In case you need to deal with a broken backend,
    * this library allows you to do so anyway. **Beware that in this case, the JVM can still enforce a compliant HTTP
    * method**.
    */
  def withBody(body: BodyPart): HttpRequest = {
    withHeader("Content-Type", body.contentType).copy(body = Some(body))
  }


  private def _send[T <: HttpResponse](factory: HttpResponseFactory[T])(implicit scheduler: Scheduler): Future[T] = {
    val promise = Promise[T]

    val timeoutTask = scheduler.scheduleOnce(timeout.length, timeout.unit,
      new Runnable {
        override def run(): Unit = {
          promise.tryFailure(new TimeoutException)
        }
      })

    val backendFuture: Future[T] = HttpDriver.send(this, factory)
    backendFuture.onComplete({response =>
      timeoutTask.cancel()
      promise.tryComplete(response)
    })

    promise.future
  }

  def stream()(implicit scheduler: Scheduler): Future[StreamHttpResponse] =
    _send(StreamHttpResponse)

  /** Sends this request.
    *
    * A request can be sent multiple times. When a request is sent, it returns a Future[HttpResponse]
    * which either succeeds with an [[HttpResponse]] or fails.]]
    */
  def send()(implicit scheduler: Scheduler): Future[SimpleHttpResponse] =
  _send(SimpleHttpResponse)

  /** Sends this request with the GET method.
    *
    * @see [[send]]
    */
  def get()(implicit scheduler: Scheduler): Future[SimpleHttpResponse] =
    withMethod(Method.GET).send()

  /** Sends this request with the POST method and a body
    *
    * @see [[send]]
    * @param body The body to send with the request
    */
  def post(body: BodyPart)(implicit scheduler: Scheduler): Future[SimpleHttpResponse] =
      withMethod(Method.POST).send(body)

  /** Sends this request with the PUT method and a body
    *
    * @see [[post]]
    * @param body The body to send with the request
    */
  def put(body: BodyPart)(implicit scheduler: Scheduler): Future[SimpleHttpResponse] =
      withMethod(Method.PUT).send(body)

  /** Sends this request with the OPTIONS method and a body
    *
    * @see [[post]]
    * @param body The body to send with the request
    */
  def options(body: BodyPart)(implicit scheduler: Scheduler): Future[SimpleHttpResponse] =
      withMethod(Method.OPTIONS).send(body)

  /** Sends this request with a body.
    *
    * This method should not be used directly. If you want to [[post]] or [[put]]
    * some data, you should use the appropriate methods. If you do not want to send
    * data with the request, you should use [[post]] without arguments.
    *
    * @param body The body to send.
    */
  def send(body: BodyPart)(implicit scheduler: Scheduler): Future[SimpleHttpResponse] =
      withBody(body).send()

  /** Internal method to back public facing .withXXX methods. */
  private def copy(
      method: Method      = this.method,
      host: String        = this.host,
      path: String        = this.path,
      port: Option[Int]   = this.port,
      protocol: Protocol  = this.protocol,
      queryString: Option[String] = this.queryString,
      headers: HeaderMap[String]  = this.headers,
      credentials: Boolean = this.credentials,
      body: Option[BodyPart] = this.body,
      backendConfig: BackendConfig = this.backendConfig,
      timeout: FiniteDuration = this.timeout
  ): HttpRequest = {
    new HttpRequest(
      method    = method,
      host      = host,
      path      = path,
      port      = port,
      protocol  = protocol,
      queryString = queryString,
      credentials = credentials,
      headers   = headers,
      body      = body,
      backendConfig = backendConfig,
      timeout   = timeout)
  }

}

object HttpRequest {

  private def default = new HttpRequest(
    method = Method.GET,
    host = null,
    path = null,
    port = None,
    protocol = Protocol.HTTP,
    queryString = None,
    headers = HeaderMap(),
    credentials = false,
    body = None,
    backendConfig = BackendConfig(),
    timeout = FiniteDuration(30, TimeUnit.SECONDS)
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
