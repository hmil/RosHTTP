package fr.hmil.roshttp

import java.net.{HttpURLConnection, URL}
import java.nio.ByteBuffer

import fr.hmil.roshttp.exceptions.{HttpNetworkException, HttpResponseException}
import fr.hmil.roshttp.response.{HttpResponse, HttpResponseFactory}
import fr.hmil.roshttp.util.HeaderMap
import monifu.concurrent.Scheduler
import monifu.reactive.Observable

import scala.concurrent.{Future, blocking}


private object HttpDriver extends DriverTrait {

  def send[T <: HttpResponse](req: HttpRequest, responseFactory: HttpResponseFactory[T])(implicit scheduler: Scheduler):
      Future[T] = {
    concurrent.Future {
      blocking {
        try {
          val connection = prepareConnection(req)
          readResponse(connection, responseFactory, req.backendConfig)
        } catch {
          case e: HttpResponseException => throw e
          case e: Throwable => {
            e.printStackTrace()
            throw new HttpNetworkException(e)
          }
        }
      }
    }.flatMap(f => f)
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
      connection: HttpURLConnection, responseFactory: HttpResponseFactory[T], config: BackendConfig)
      (implicit scheduler: Scheduler): Future[T] = {
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

    if (code < 400) {
      responseFactory(
        code,
        headerMap,
        inputStreamToObservable(connection.getInputStream, config.maxChunkSize),
        config
      )
    } else {
      responseFactory(
        code,
        headerMap,
        Option(connection.getErrorStream)
          .map(is => inputStreamToObservable(is, config.maxChunkSize))
          .getOrElse(Observable.from(ByteBuffer.allocate(0))),
        config
      ).map(response => throw HttpResponseException.badStatus(response))
    }
  }

  private def inputStreamToObservable(in: java.io.InputStream, chunkSize: Int): Observable[ByteBuffer] = {
    val iterator = new Iterator[ByteBuffer] {
      private[this] var buffer: Array[Byte] = null
      private[this] var lastCount = 0

      def hasNext: Boolean =
        lastCount match {
          case 0 =>
            buffer = new Array[Byte](chunkSize)
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
