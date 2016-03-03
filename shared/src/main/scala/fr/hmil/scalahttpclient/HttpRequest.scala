package fr.hmil.scalahttpclient

import scala.concurrent.{Promise, Future}

class HttpRequest {

  def setURL(url: String): HttpRequest = {

    this
  }

  def setMethod(method: String): HttpRequest = {
    this
  }

  def send: Future[HttpResponse] = {
    HttpDriver.send(this)
  }
}
