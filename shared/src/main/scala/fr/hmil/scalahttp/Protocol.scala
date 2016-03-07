package fr.hmil.scalahttp


final case class Protocol private(name: String) {

  override implicit def toString: String = name
}

object Protocol {
  val HTTP = Protocol("HTTP")
  val HTTPS = Protocol("HTTPS")
}

