package fr.hmil.roshttp

/** Defines the protocol used.
  *
  * When setting a protocol from a string, we want to preserve the initial case such as
  * not to alter the url.
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
  val HTTP = fromString("http")
  val HTTPS = fromString("https")

  def fromString(name: String): Protocol = name.toUpperCase match {
    case "HTTP" => Protocol(name)
    case "HTTPS" => Protocol(name)
    case _ => throw new IllegalArgumentException(s"Invalid protocol: $name")
  }
}
