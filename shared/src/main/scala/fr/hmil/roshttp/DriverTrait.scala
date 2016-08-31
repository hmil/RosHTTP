package fr.hmil.roshttp

import fr.hmil.roshttp.response.{HttpResponse, HttpResponseFactory}
import monifu.concurrent.Scheduler

import scala.concurrent.Future

private trait DriverTrait {
  def send[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T])(implicit scheduler: Scheduler):
      Future[T]
}
