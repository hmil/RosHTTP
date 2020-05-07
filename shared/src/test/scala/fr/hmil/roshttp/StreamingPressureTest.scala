package fr.hmil.roshttp

import java.nio.ByteBuffer

import fr.hmil.roshttp.body.StreamBody
import monix.reactive.Observable
import monix.execution.Scheduler.Implicits.global
import utest._
import monix.eval.Task

object StreamingPressureTest extends TestSuite {

  private val SERVER_URL = "http://localhost:3000"

  private val ONE_MILLION = 1000000

  val tests = this{

    // Send approx. 8 gigs of data to the server to check that there is no leak
    "Upload streams do not leak" - {
      if (!JsEnvUtils.isRealBrowser) {
        HttpRequest(s"$SERVER_URL/streams/in")
          .post(StreamBody(Observable.fromIterator(Task(new Iterator[ByteBuffer]() {
            override def hasNext: Boolean = true
            override def next(): ByteBuffer = ByteBuffer.allocateDirect(8192)
          }))
            .take(ONE_MILLION)))
          .map(r => r.body ==> "Received 8192000000 bytes.")
          .recover({
            case e: Throwable =>
              e.printStackTrace()
          })
      }
    }

    // Receive approx. 8 gigs of data to ensure that there is no leak
    "Download streams do not leak" - {
      // Due to browser incompatibility and node memory leak, run this test only in the JVM
      if (JsEnvUtils.userAgent == "jvm") {
        HttpRequest(s"$SERVER_URL/streams/out")
          .stream()
          .flatMap(_.body.map(_.limit().asInstanceOf[Long]).reduce((l, r) => l + r).runAsyncGetFirst)
          .map(_.get ==> 8192000000L)
      }
    }
  }
}
