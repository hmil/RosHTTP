package fr.hmil.roshttp

import java.net.{HttpURLConnection, URL}
import java.nio.ByteBuffer

import fr.hmil.roshttp.exceptions._
import fr.hmil.roshttp.response.{HttpResponse, HttpResponseFactory, HttpResponseHeader}
import fr.hmil.roshttp.util.HeaderMap
import monix.execution.Ack.Continue
import monix.execution.{Ack, Scheduler}
import monix.reactive.{Observable, Observer}

import scala.concurrent.{Future, Promise, blocking}


private object HttpDriver extends DriverTrait {

  def send[T <: HttpResponse]
      (req: HttpRequest, responseFactory: HttpResponseFactory[T])
      (implicit scheduler: Scheduler): Future[T] = {

    sendRequest(req).flatMap({ connection =>
        readResponse(connection, responseFactory, req.backendConfig)})
  }

  private def sendRequest(req: HttpRequest)(implicit scheduler: Scheduler): Future[HttpURLConnection] = {
    val p = Promise[HttpURLConnection]()
    val connection = new URL(req.url).openConnection().asInstanceOf[HttpURLConnection]
    req.headers.foreach(t => connection.addRequestProperty(t._1, t._2))
    connection.setRequestMethod(req.method.toString)
    if (req.body.isDefined) {
      req.body.foreach({ part =>
        connection.setDoOutput(true)
        connection.setChunkedStreamingMode(4096) // TODO move magic to backend config

        val os = connection.getOutputStream
        part.content.subscribe(new Observer[ByteBuffer] {
          override def onError(ex: Throwable): Unit = {
            os.close()
            p.failure(new UploadStreamException(ex))
          }
          override def onComplete(): Unit = {
            os.close()
            p.success(connection)
          }
          override def onNext(buffer: ByteBuffer): Future[Ack] = {
            if (buffer.hasArray) {
              os.write(buffer.array().view(0, buffer.limit).toArray)
            } else {
              val tmp = new Array[Byte](buffer.limit)
              buffer.get(tmp)
              os.write(tmp)
            }
            Continue
          }
        })
      })
    } else {
      p.success(connection)
    }
    p.future
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

    val header = new HttpResponseHeader(code, headerMap)

    Future {
      blocking {
        if (code < 400) {
          responseFactory(
            header,
            inputStreamToObservable(connection.getInputStream, config.maxChunkSize),
            config
          )
        } else {
          responseFactory(
            header,
            Option(connection.getErrorStream)
              .map(is => inputStreamToObservable(is, config.maxChunkSize))
              .getOrElse(Observable.eval(ByteBuffer.allocate(0))),
            config
          ).map(response => throw HttpException.badStatus(response))
        }
      }
    }.flatMap(f => f)
  }

  private def inputStreamToObservable(in: java.io.InputStream, chunkSize: Int): Observable[ByteBuffer] = {
    val iterator = new Iterator[ByteBuffer] {
      private var buffer: Array[Byte] = _
      private var lastCount = 0

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
