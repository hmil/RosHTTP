package fr.hmil.scalahttp.body

trait BodyPart {
  val contentType: String
  val contentLength: Int
  val content: Array[Byte]
}
