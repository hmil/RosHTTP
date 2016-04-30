package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.body.BodyPart
import fr.hmil.scalahttp.node.Modules.HttpModule

import scala.concurrent.Future

private object HttpDriver {

  def send(req: HttpRequest, body: Option[BodyPart]): Future[HttpResponse] = {
    if (HttpModule.isAvailable) {
      NodeDriver.send(req, body)
    } else {
      BrowserDriver.send(req, body)
    }
  }
}
