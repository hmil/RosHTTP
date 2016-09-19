package fr.hmil.roshttp

import java.nio.ByteBuffer

import fr.hmil.roshttp.ByteBufferQueue.Feeder
import monix.execution.{Ack, Cancelable}
import monix.execution.Ack.{Continue, Stop}
import monix.reactive.Observable
import monix.reactive.observers.Subscriber

import scala.collection.mutable
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

// TODO: doc this
private[roshttp] class ByteBufferQueue(
    private val capacity: Int,
    private val feeder: Feeder = ByteBufferQueue.noopFeeder)
    (implicit ec: ExecutionContext) {

  private var subscriber: Option[Subscriber[ByteBuffer]] = None
  private val bufferQueue = mutable.Queue[ByteBuffer]()
  private var hasEnd = false
  private var isWaitingForAck = false
  private var error: Throwable = _

  private val cancelable = new Cancelable {
    override def cancel(): Unit = stop()
  }

  def propagate(): Unit = subscriber.foreach({ subscriber =>
    if (!isWaitingForAck) {
      if (bufferQueue.nonEmpty) {
        isWaitingForAck = true
        val wasFull = isFull
        subscriber.onNext(bufferQueue.dequeue()).onComplete(handleAck)
        if (wasFull) {
          feeder.onFlush()
        }
      } else if (hasEnd) {
        if (error != null) {
          subscriber.onError(error)
        }
        stop()
      }
    }
  })

  def handleAck(ack: Try[Ack]): Unit = {
    isWaitingForAck = false
    ack match {
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
  }

  def push(buffer: ByteBuffer): Unit = {
    if (hasEnd) throw new IllegalStateException("Trying to push new data to an ended buffer queue")
    if (isFull) throw new IllegalStateException("Buffer queue is full")
    bufferQueue.enqueue(buffer)
    if (isFull) {
      feeder.onFull()
    }
    if (bufferQueue.nonEmpty) {
      propagate()
    }
  }

  def end(): Unit = {
    hasEnd = true
    if (bufferQueue.isEmpty) {
      stop()
    }
  }

  def isFull: Boolean = {
    bufferQueue.length == capacity
  }

  def pushError(error: Throwable): Unit = {
    this.error = error
    this.hasEnd = true
    propagate()
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

  def length: Int = {
    bufferQueue.length
  }
}

object ByteBufferQueue {
  trait Feeder {
    def onFull(): Unit
    def onFlush(): Unit
  }

  private val noopFeeder = new Feeder {
    override def onFlush(): Unit = ()
    override def onFull(): Unit = ()
  }
}