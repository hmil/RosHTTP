package fr.hmil.scalahttp.client

/**
  * TODO
  */
class HttpClient {

  def get(url: String): HttpRequest = {
    val req = HttpRequest.create
        .withMethod("GET")
        .withURL(url)
    req
  }
}
