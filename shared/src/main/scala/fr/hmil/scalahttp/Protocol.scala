package fr.hmil.scalahttp

/** Defines the protocol used.
  *
  * When setting a protocol from a string, we want to preserve the initial case such as
  * not to alter the url.
  */
final case class Protocol private(private val name: String, defaultPort: Int) {

  override implicit def toString: String = name

  override def equals(o: Any): Boolean = o match {
    case that: Protocol => that.name.equalsIgnoreCase(this.name)
    case _ => false
  }

  override def hashCode: Int = name.hashCode
}

object Protocol {
  val HTTP = fromString("http")
  val HTTPS = fromString("https")

  def fromString(name: String): Protocol = name.toUpperCase match {
    case "HTTP" => Protocol(name, 80)
    case "HTTPS" => Protocol(name, 443)
    case _ => throw new IllegalArgumentException(s"Invalid protocol: $name")
  }
}
