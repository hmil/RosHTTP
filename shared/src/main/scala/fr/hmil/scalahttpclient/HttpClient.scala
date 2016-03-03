package fr.hmil.scalahttpclient

/**
  * TODO
  */
class HttpClient {

  def get(url: String): HttpRequest = {
    val req = new HttpRequest()
    req.setMethod("GET")
    req.setURL(url)
    req
  }
}
