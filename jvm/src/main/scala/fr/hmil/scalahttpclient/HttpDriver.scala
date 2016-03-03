package fr.hmil.scalahttpclient

import scala.concurrent.{Promise, Future}

object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    val p: Promise[HttpResponse] = Promise[HttpResponse]()

    p.future
  }
}
