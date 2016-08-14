package fr.hmil.roshttp

import scala.concurrent.Future

private trait DriverTrait {
  def send(req: HttpRequest): Future[HttpResponse]
}
