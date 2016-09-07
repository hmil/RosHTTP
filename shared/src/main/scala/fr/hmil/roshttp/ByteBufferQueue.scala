package fr.hmil.roshttp

import java.nio.ByteBuffer

import monix.execution.{Ack, Cancelable}
import monix.execution.Ack.{Continue, Stop}
import monix.reactive.Observable
import monix.reactive.observers.Subscriber

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

private[roshttp] class ByteBufferQueue(implicit ec: ExecutionContext) {
  var subscriber: Option[Subscriber[ByteBuffer]] = None
  val bufferQueue = mutable.Queue[ByteBuffer]()
  var hasEnd = false

  private val cancelable = new Cancelable {
    override def cancel(): Unit = stop()
  }

  def propagate(): Unit = subscriber.foreach({ subscriber =>
    if (bufferQueue.nonEmpty) {
      subscriber.onNext(bufferQueue.dequeue()).onComplete(handleAck)
    } else if (hasEnd) {
      stop()
    }
  })

  def handleAck(ack: Try[Ack]): Unit = ack match {
    case Success(Stop) =>
      subscriber = None
    case Success(Continue) =>
      if (bufferQueue.nonEmpty) {
        propagate()
      } else if (hasEnd) {
        stop()
      }
    case Failure(ex) =>
      subscriber = None
      subscriber.foreach(_.onError(ex))
  }

  def push(buffers: Seq[ByteBuffer]): Unit = {
    if (hasEnd) throw new IllegalStateException("Trying to push new data to an ended buffer queue")
    bufferQueue.enqueue(buffers:_*)
    if (bufferQueue.nonEmpty) {
      subscriber.foreach(_ => propagate())
    }
  }

  def push(buffer: ByteBuffer): Unit = {
    push(Seq(buffer))
  }

  def end(): Unit = {
    hasEnd = true
    if (bufferQueue.isEmpty) {
      stop()
    }
  }

  val observable = new Observable[ByteBuffer]() {
    override def unsafeSubscribeFn(sub: Subscriber[ByteBuffer]): Cancelable = {
      if (subscriber.isDefined) {
        throw new IllegalStateException("A subscriber is already defined")
      }
      subscriber = Some(sub)
      if (bufferQueue.nonEmpty) {
        propagate()
      } else if (hasEnd) {
        stop()
      }
      cancelable
    }
  }

  private def stop(): Unit = {
    subscriber.foreach(_.onComplete())
  }
}
