package fr.hmil.scalahttp.client

import java.io.PrintWriter
import java.net.{HttpURLConnection, URL}

import fr.hmil.scalahttp.HttpUtils
import fr.hmil.scalahttp.body.BodyPart

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source

private object HttpDriver {

  def send(req: HttpRequest, body: Option[BodyPart]): Future[HttpResponse] = {
    concurrent.Future {
      try {
        val connection = prepareConnection(req, body)
        readResponse(connection)
      } catch {
        case e: HttpResponseError => throw e
        case e: Throwable => throw new HttpNetworkError(e)
      }
    }
  }

  private def prepareConnection(req: HttpRequest, body: Option[BodyPart]): HttpURLConnection = {
    val connection = new URL(req.url).openConnection().asInstanceOf[HttpURLConnection]
    req.headers.foreach(t => connection.addRequestProperty(t._1, t._2))
    connection.setRequestMethod(req.method.toString)
    body.foreach({part =>
      connection.setDoOutput(true)
      val os = connection.getOutputStream
      os.write(part.content)
      os.close()
    })
    connection
  }

  private def readResponse(connection: HttpURLConnection): HttpResponse = {
    val code = connection.getResponseCode
    val headerMap = HeaderMap(Iterator.from(0)
      .map(i => (i, connection.getHeaderField(i)))
      .takeWhile(_._2 != null)
      .flatMap({ t =>
        connection.getHeaderFieldKey(t._1) match {
          case null => None
          case key => Some((key, t._2.mkString.trim))
        }
      }).toMap[String, String])
    val charset = HttpUtils.charsetFromContentType(headerMap.getOrElse("content-type", null))

    if (code < 400) {
      new HttpResponse(
        code,
        Source.fromInputStream(connection.getInputStream)(charset)
          .flatMap(_.toString.getBytes(charset))
          .toArray,
        headerMap
      )
    } else {
      throw HttpResponseError.badStatus(new HttpResponse(
        code,
        Source.fromInputStream(connection.getErrorStream)(charset).flatMap(_.toString.getBytes(charset)).toArray,
        headerMap
      ))
    }
  }
}
