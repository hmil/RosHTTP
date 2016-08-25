package fr.hmil.roshttp

import fr.hmil.roshttp.node.Modules.{HttpModule, HttpsModule}

import scala.concurrent.{ExecutionContext, Future}

private object HttpDriver extends DriverTrait {

  private var _driver: Option[DriverTrait] = None

  def send(req: HttpRequest)(implicit ec: ExecutionContext): Future[HttpResponse] = {
    _driver.getOrElse(chooseBackend()).send(req)
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
