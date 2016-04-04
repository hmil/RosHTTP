package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.node.Modules.HttpModule

import scala.concurrent.Future

object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    if (HttpModule.isAvailable) {
      NodeDriver.send(req)
    } else {
      BrowserDriver.send(req)
    }
  }
}
