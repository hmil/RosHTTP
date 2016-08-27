package fr.hmil.roshttp

import scala.concurrent.{ExecutionContext, Future}

private trait DriverTrait {
  def send[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T])(implicit ec: ExecutionContext):
      Future[T]
}
