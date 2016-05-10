package fr.hmil.scalahttp.body

object Implicits {
  implicit def stringToTextBody(s: String): TextBody = new TextBody(s)
  implicit def streamToStreamBody(s: Stream[Byte]): StreamBody = new StreamBody(s)
}
