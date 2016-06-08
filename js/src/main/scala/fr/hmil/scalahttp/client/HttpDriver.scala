package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.node.Modules.{HttpModule, HttpsModule}

import scala.concurrent.Future

private object HttpDriver extends AbstractDriver {

  private var _driver: Option[AbstractDriver] = None

  def send(req: HttpRequest): Future[HttpResponse] = {
    _driver.getOrElse(chooseBackend()).send(req)
  }

  private def chooseBackend(): AbstractDriver = {
    if (HttpModule.isAvailable && HttpsModule.isAvailable) {
      _driver = Some(NodeDriver)
    } else {
      _driver = Some(BrowserDriver)
    }
    _driver.get
  }
}
