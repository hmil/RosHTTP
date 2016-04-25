package fr.hmil.scalahttp

/**
 * Defines the protocol used. For now, only HTTP is officialy supported.
 */
final case class Protocol private(private val name: String) {

  override implicit def toString: String = name

  override def equals(o: Any): Boolean = o match {
    case that: Protocol => that.name.equalsIgnoreCase(this.name)
    case _ => false
  }

  override def hashCode: Int = name.hashCode
}

object Protocol {
  val HTTP = Protocol("HTTP")
  val HTTPS = Protocol("HTTPS")

  implicit def fromString(name: String): Protocol = name.toUpperCase match {
    case "HTTP" => Protocol(name)
    case "HTTPS" => Protocol(name)
    case _ => throw new IllegalArgumentException(s"Invalid protocol: $name")
  }
}
