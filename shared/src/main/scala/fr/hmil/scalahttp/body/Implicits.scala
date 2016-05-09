package fr.hmil.scalahttp.body

object Implicits {
  implicit def stringToTextPart(s: String): TextBody = new TextBody(s)
}
