package fr.hmil.scalahttpclient

import fr.hmil.scalahttpclient.node.Modules.http
import fr.hmil.scalahttpclient.node.http.{IncomingMessage, RequestOptions}

import scala.concurrent.{Future, Promise}

object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    val req = http.request(RequestOptions(
      hostname = "blog.hmil.fr",
      port = 80,
      method = "GET",
      path = "/"
    ), (message: IncomingMessage) => {
      println("HTTP/" + message.httpVersion + " " + message.statusCode + " " + message.statusMessage)
    })

    req.end()
    p.future
  }
}
