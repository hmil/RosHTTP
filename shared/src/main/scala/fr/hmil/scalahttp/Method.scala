package fr.hmil.scalahttp

/**
  * Represents an HTTP method as per the
  * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html" target="_blank">
  * http method specification</a>.
  */
final case class Method private (name: String) {

  override implicit def toString: String = name

  override def equals(o: Any): Boolean = o match {
    case that: Method => that.name.equalsIgnoreCase(this.name)
    case _ => false
  }

  override def hashCode: Int = name.toUpperCase.hashCode
}

object Method {
  val OPTIONS = Method("OPTIONS")
  val GET = Method("GET")
  val HEAD = Method("HEAD")
  val POST = Method("POST")
  val PUT = Method("PUT")
  val DELETE = Method("DELETE")
  val TRACE = Method("TRACE")
  val CONNECT = Method("CONNECT")
  implicit def fromString(name: String): Method = new Method(name)
}
