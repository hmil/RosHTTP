package fr.hmil.scalahttp


final case class Protocol private(name: String)

object Protocol {
  val HTTP = Protocol("HTTP")
  val HTTPS = Protocol("HTTPS")
}
