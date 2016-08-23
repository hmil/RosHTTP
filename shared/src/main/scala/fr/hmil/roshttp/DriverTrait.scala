package fr.hmil.roshttp

import scala.concurrent.{ExecutionContext, Future}

private trait DriverTrait {
  def send(req: HttpRequest)(implicit ec: ExecutionContext): Future[HttpResponse]
}
