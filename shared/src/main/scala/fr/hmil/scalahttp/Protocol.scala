package fr.hmil.scalahttp

/**
 * Defines the protocol used. For now, only HTTP is officialy supported.
 */
final case class Protocol private(name: String) {

  override implicit def toString: String = name

  override def equals(o: Any): Boolean = o match {
    case that: Method => that.name.equalsIgnoreCase(this.name)
    case _ => false
  }

  override def hashCode: Int = name.toUpperCase.hashCode
}

object Protocol {
  val HTTP = Protocol("http")
  val HTTPS = Protocol("https")

  implicit def fromString(name: String): Protocol = new Protocol(name)
}
