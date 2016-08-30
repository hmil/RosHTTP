package fr.hmil.roshttp.body

import java.nio.ByteBuffer

import fr.hmil.roshttp.ByteBufferQueue
import monix.execution.Ack.Continue
import monix.execution.{Ack, Scheduler}
import monix.reactive.{Observable, Observer}

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Random, Success}

/** A body made of multiple parts.
  *
  * <b>Usage:</b> A multipart body acts as a container for other bodies. For instance,
  * the multipart body is commonly used to send a form with binary attachments in conjunction with
  * the [[ByteBufferBody]].
  * For simple key/value pairs, use [[URLEncodedBody]] instead.
  *
  * <b>Safety consideration:</b> A random boundary is generated to separate parts. If the boundary was
  * to occur within a body part, it would mess up the whole body. In practice, the odds are extremely small though.
  *
  * @param parts The pieces of body. The key in the map is used as `name` for the `Content-Disposition` header
  *              of each part.
  * @param subtype The exact multipart mime type as in `multipart/subtype`. Defaults to `form-data`.
  */
class MultiPartBody(parts: Map[String, BodyPart], subtype: String = "form-data")(implicit scheduler: Scheduler)
  extends BodyPart {

  val boundary = "----" + Random.alphanumeric.take(24).mkString.toLowerCase

  private val LINE_BREAK = ByteBuffer.wrap(Array[Byte](13, 10))

  override def contentType: String = s"multipart/$subtype; boundary=$boundary"

  // TODO use combinations on observables

  override def content: Observable[ByteBuffer] = {
    val queue = new ByteBufferQueue()

    parts.map({ case(name, part) =>
      val localBuffer = new ByteBufferQueue()
      val p = Promise[(String, BodyPart, ByteBufferQueue)]()
      part.content.subscribe(new Observer[ByteBuffer] {

        override def onError(ex: Throwable): Unit = {
          p.failure(ex)
        }

        override def onComplete(): Unit = {
          p.success((name, part, localBuffer))
        }

        override def onNext(buffer: ByteBuffer): Future[Ack] = {
          localBuffer.push(buffer)
          Continue
        }
      })
      p.future
    }).foldLeft(Future.successful(()))({(acc, f) =>

      acc.flatMap({ _ =>
        f.map({ case (name, part, buffer) =>
          val p = Promise[Unit]
          queue.push(ByteBuffer.wrap(
            ("\r\n--" + boundary + "\r\n" +
              "Content-Disposition: form-data; name=\"" + name + "\"\r\n" +
              s"Content-Type: ${part.contentType}\r\n" +
              "\r\n").getBytes("utf-8")))

          buffer.observable.subscribe(new Observer[ByteBuffer] {

            override def onError(ex: Throwable): Unit = {
              p.failure(ex)
            }

            override def onComplete(): Unit = {
              p.success(())
            }

            override def onNext(buffer: ByteBuffer): Future[Ack] = {
              queue.push(buffer)
              Continue
            }
          })
          p.future
        })
      })
    }).andThen({
      case _: Success[Unit] =>
        queue.push(ByteBuffer.wrap(s"\r\n--$boundary--".getBytes("utf-8")))
        queue.end()
      case f: Failure[Unit] =>
        queue.end()
    })

    queue.observable
  }
}

object MultiPartBody {
  def apply(parts: (String, BodyPart)*)(implicit scheduler: Scheduler): MultiPartBody =
    new MultiPartBody(Map(parts: _*))
}
