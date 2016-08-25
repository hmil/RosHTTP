package fr.hmil.roshttp

import scala.concurrent.Future

private trait DriverTrait {
  def send[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T]): Future[T]
}
