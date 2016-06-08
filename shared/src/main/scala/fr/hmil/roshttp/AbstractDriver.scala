package fr.hmil.roshttp

import scala.concurrent.Future

private trait AbstractDriver {
  def send(req: HttpRequest): Future[HttpResponse]
}
