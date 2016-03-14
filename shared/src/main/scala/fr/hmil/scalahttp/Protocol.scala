package fr.hmil.scalahttp


final case class Protocol private(name: String) {

  override implicit def toString: String = name
}

object Protocol {
  val HTTP = Protocol("http")
  val HTTPS = Protocol("https")
}

