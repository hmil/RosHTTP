package fr.hmil.scalahttp.client

import scala.concurrent.Future

private trait AbstractDriver {
  def send(req: HttpRequest): Future[HttpResponse]
}
