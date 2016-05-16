package fr.hmil.scalahttp.body

object Implicits {
  implicit def stringToTextBody(s: String): StringBody = new StringBody(s)
  implicit def streamToStreamBody(s: Stream[Byte]): StreamBody = new StreamBody(s)
}
