package fr.hmil.scalahttp.client

import fr.hmil.scalahttp.JsEnvUtils
import fr.hmil.scalahttp.body.BodyPart

import scala.concurrent.Future

private object HttpDriver {

  def send(req: HttpRequest): Future[HttpResponse] = {
    if (JsEnvUtils.isRealBrowser) {
      BrowserDriver.send(req)
    } else {
      NodeDriver.send(req)
    }
  }
}
