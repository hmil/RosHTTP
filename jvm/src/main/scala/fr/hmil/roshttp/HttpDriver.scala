package fr.hmil.roshttp

import java.net.{HttpURLConnection, URL}
import java.nio.ByteBuffer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}
import monifu.reactive.Observable

private object HttpDriver extends DriverTrait {

  def send[T <: HttpResponse](req: HttpRequest, responseFactory: HttpResponseFactory[T]): Future[T] = {

    concurrent.Future {
      try {
        blocking {
          val connection = prepareConnection(req)
          readResponse(connection, responseFactory)
        }
      } catch {
        case e: HttpResponseError => throw e
        case e: Throwable => throw new HttpNetworkError(e)
      }
    }
  }

  private def prepareConnection(req: HttpRequest): HttpURLConnection = {
    val connection = new URL(req.url).openConnection().asInstanceOf[HttpURLConnection]
    req.headers.foreach(t => connection.addRequestProperty(t._1, t._2))
    connection.setRequestMethod(req.method.toString)
    req.body.foreach({part =>
      connection.setDoOutput(true)
      val os = connection.getOutputStream
      os.write(part.content.array())
      os.close()
    })
    connection
  }

  private def readResponse[T <: HttpResponse](
      connection: HttpURLConnection, responseFactory: HttpResponseFactory[T]): T = {
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
      responseFactory(
        code,
        inputStreamToObservable(connection.getInputStream),
        headerMap
      )
    } else {
      throw HttpResponseError.badStatus(responseFactory(
        code,
        inputStreamToObservable(connection.getErrorStream),
        headerMap
      ))
    }
  }

  // TODO: configure chunk size
  private def inputStreamToObservable(in: java.io.InputStream, chunkSize: Int = 1024): Observable[ByteBuffer] = {
    val iterator = new Iterator[ByteBuffer] {
      private[this] val buffer = new Array[Byte](chunkSize)
      private[this] var lastCount = 0

      def hasNext: Boolean =
        lastCount match {
          case 0 =>
            lastCount = in.read(buffer)
            lastCount >= 0
          case nr =>
            nr >= 0
        }

      def next(): ByteBuffer = {
        if (lastCount < 0)
          throw new NoSuchElementException
        else {
          val result = ByteBuffer.wrap(buffer, 0, lastCount)
          lastCount = 0
          result
        }
      }
    }

    Observable.fromIterator(iterator)
  }
}
