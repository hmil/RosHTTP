package fr.hmil.roshttp

import java.nio.ByteBuffer

import monifu.reactive.Ack.{Cancel, Continue}
import monifu.reactive.{Ack, Observable, Observer, Subscriber}

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

private[roshttp] class ByteBufferQueue(implicit ec: ExecutionContext) {
  var subscriber: Option[Subscriber[ByteBuffer]] = None
  val bufferQueue = mutable.Queue[ByteBuffer]()
  var hasEnd = false

  def propagate(): Unit = subscriber.foreach({ subscriber =>
    if (bufferQueue.nonEmpty) {
      subscriber.onNext(bufferQueue.dequeue()).onComplete(handleAck)
    } else if (hasEnd) {
      stop()
    }
  })

  def handleAck(ack: Try[Ack]): Unit = ack match {
    case Success(Cancel) =>
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
    override def onSubscribe(sub: Subscriber[ByteBuffer]): Unit = {
      if (subscriber.isDefined) {
        throw new IllegalStateException("A subscriber is already defined")
      }
      subscriber = Some(sub)
      if (bufferQueue.nonEmpty) {
        propagate()
      }
    }
  }

  private def stop(): Unit = {
    subscriber.foreach(_.onComplete())
    subscriber = None
  }
}
