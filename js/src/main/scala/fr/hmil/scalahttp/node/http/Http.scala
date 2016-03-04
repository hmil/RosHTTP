package fr.hmil.scalahttp.node.http

import fr.hmil.scalahttp.node.Module

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

/**
  * The node http API.
  *
  * Server-related stuff not included.
  * createClient not included because it is deprecated.
  *
  * @see https://nodejs.org/api/http.html
  */
@js.native
trait Http extends js.Object{

  /**
    * A list of the HTTP methods that are supported by the parser.
    */
  val METHODS: Seq[String] = js.native

  /**
    * A collection of all the standard HTTP response status codes, and the short
    * description of each. For example, http.STATUS_CODES[404] === 'Not Found'.
    */
  val STATUS_CODES: Map[Number, String] = js.native

  /**
    * Global instance of Agent which is used as the default for all http client requests.
    */
  val globalAgent: Agent = js.native

  // http.createServer([requestListener]) -- server-side stuff, not needed in this project
  // http.createClient([port][, host]) -- deprecated API, not implemented

  /**
    * Since most requests are GET requests without bodies, Node.js provides this convenience
    * method. The only difference between this method and http.request() is that it sets the
    * method to GET and calls req.end() automatically.
    */
  def get(url: String): ClientRequest = js.native
  def get(url: String, cb: js.Function1[IncomingMessage, Unit]): ClientRequest = js.native
  def get(options: RequestOptions): ClientRequest = js.native
  def get(options: RequestOptions, cb: js.Function1[IncomingMessage, Unit]): ClientRequest = js.native

  /**
    * Node.js maintains several connections per server to make HTTP requests. This function
    * allows one to transparently issue requests.
    * options can be an object or a string. If options is a string, it is automatically
    * parsed with url.parse().
    */
  def request(url: String): ClientRequest = js.native
  def request(url: String, cb: js.Function1[IncomingMessage, Unit]): ClientRequest = js.native
  def request(options: RequestOptions): ClientRequest = js.native
  def request(options: RequestOptions, cb: js.Function1[IncomingMessage, Unit]): ClientRequest = js.native
}

@js.native
@JSName("http")
object Http extends Http

object HttpModule extends Module("http", Http)
