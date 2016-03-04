package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.node.Modules.http
import fr.hmil.scalahttp.node.http.{IncomingMessage, RequestOptions}

import scala.concurrent.{Future, Promise}

object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    val nodeRequest = http.request(RequestOptions(
      hostname = req.host,
      port = req.port,
      method = req.method.name,
      path = req.path
    ), (message: IncomingMessage) => {
      println("HTTP/" + message.httpVersion + " " + message.statusCode + " " + message.statusMessage)
    })

    nodeRequest.end()
    p.future
  }
}
