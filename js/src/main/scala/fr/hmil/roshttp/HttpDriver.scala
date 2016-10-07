package fr.hmil.roshttp

import fr.hmil.roshttp.node.Modules.{HttpModule, HttpsModule}
import fr.hmil.roshttp.response.{HttpResponse, HttpResponseFactory}
import monix.execution.Scheduler

import scala.concurrent.Future

private object HttpDriver extends DriverTrait {

  private var _driver: Option[DriverTrait] = None

  def send[T <: HttpResponse](req: HttpRequest, factory: HttpResponseFactory[T])(implicit scheduler: Scheduler):
      Future[T] = {
    _driver.getOrElse(chooseBackend()).send(req, factory)
  }

  private def chooseBackend(): DriverTrait = {
    if (HttpModule.isAvailable && HttpsModule.isAvailable) {
      _driver = Some(NodeDriver)
    } else {
      _driver = Some(BrowserDriver)
    }
    _driver.get
  }
}
