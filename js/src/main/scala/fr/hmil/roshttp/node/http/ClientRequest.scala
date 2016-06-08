package fr.hmil.roshttp.node.http

import fr.hmil.roshttp.node.events.EventEmitter
import fr.hmil.roshttp.node.buffer.Buffer

import scala.scalajs.js

/**
  * Complete nodejs http ClientRequest API facade
  */
@js.native
private[roshttp] class ClientRequest extends EventEmitter {

  /**
    * Marks the request as aborting. Calling this will cause remaining data in
    * the response to be dropped and the socket to be destroyed.
    */
  def abort(): Unit = js.native

  /**
    * Finishes sending the request. If any parts of the body are unsent, it will
    * flush them to the stream. If the request is chunked, this will send the
    * terminating '0\r\n\r\n'.
    *
    * If data is specified, it is equivalent to calling response.write(data, encoding)
    * followed by request.end(callback).
    *
    * If callback is specified, it will be called when the request stream is finished.
    */
  def end(): Unit = js.native
  def end(data: Buffer): Unit = js.native
  def end(data: Buffer, callback: js.Function0[Unit]): Unit = js.native
  def end(data: String): Unit = js.native
  def end(data: String, encoding: String): Unit = js.native
  def end(data: String, callback: js.Function0[Unit]): Unit = js.native
  def end(data: String, encoding: String, callback: js.Function0[Unit]): Unit = js.native
  def end(callback: js.Function0[Unit]): Unit = js.native


  /**
    * Flush the request headers.
    *
    * For efficiency reasons, Node.js normally buffers the request headers until
    * you call request.end() or write the first chunk of request data. It then tries
    * hard to pack the request headers and data into a single TCP packet.
    *
    * That's usually what you want (it saves a TCP round-trip) but not when the
    * first data isn't sent until possibly much later. request.flushHeaders() lets
    * you bypass the optimization and kickstart the request.
    */
  def flushHeaders(): Unit = js.native

  /**
    * Once a socket is assigned to this request and is connected socket.setNoDelay()
    * will be called.
    */
  def setNoDelay(noDelay: Boolean): Unit = js.native
  def setNoDelay(): Unit = js.native


  /**
    * Once a socket is assigned to this request and is connected socket.setKeepAlive() will be called.
    */
  def setSocketKeepAlive(enable: Boolean, initialDelay: Int): Unit = js.native
  def setSocketKeepAlive(enable: Boolean): Unit = js.native
  def setSocketKeepAlive(initialDelay: Int): Unit = js.native

  /**
    * Once a socket is assigned to this request and is connected socket.setTimeout() will be called.
    *
    * @param timeout Milliseconds before a request is considered to be timed out.
    * @param callback Optional function to be called when a timeout occurs. Same as binding to the timeout event.
    */
  def setTimeout(timeout: Int, callback: js.Function0[Unit]): Unit = js.native
  def setTimeout(timeout: Int): Unit = js.native


  /**
    * Sends a chunk of the body. By calling this method many times, the user can stream
    * a request body to aserver--in that case it is suggested to use the ['Transfer-Encoding', 'chunked']
    * header line when creating the request.
    *
    * @param chunk should be a Buffer or a string.
    * @param encoding optional and only applies when chunk is a string. Defaults to 'utf8'.
    * @param callback optional and will be called when this chunk of data is flushed.
    * @return
    */
  def write(chunk: String, encoding: String, callback: js.Function0[Unit]): ClientRequest = js.native
  def write(chunk: String): ClientRequest = js.native
  def write(chunk: String, encoding: String): ClientRequest = js.native
  def write(chunk: String, callback: js.Function0[Unit]): ClientRequest = js.native
  def write(chunk: Buffer): ClientRequest = js.native
  def write(chunk: Buffer, callback: js.Function0[Unit]): ClientRequest = js.native

}
