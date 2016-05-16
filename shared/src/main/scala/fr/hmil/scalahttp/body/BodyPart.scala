package fr.hmil.scalahttp.body

trait BodyPart {
  val contentType: String
  val content: Array[Byte]
}
