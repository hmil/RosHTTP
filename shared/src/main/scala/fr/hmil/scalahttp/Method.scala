package fr.hmil.scalahttp

/** Wraps HTTP method strings. */
final case class Method private (private val name: String) {

  override def toString: String = name.toUpperCase

  override def equals(o: Any): Boolean = o match {
    case that: Method => that.name.equalsIgnoreCase(this.name)
    case _ => false
  }

  override def hashCode: Int = name.toUpperCase.hashCode
}

/** Exposes available methods as object as well as an implicit conversion
  * from string to Method objects.
  *
  * Because all backends do not support all methods, this library imposes a subset
  * of all available HTTP Methods. Should you find a use case for this library
  * with other HTTP methods, please submit an issue with your motivation.
  */
object Method {

  val GET = Method("GET")
  val POST = Method("POST")
  val HEAD = Method("HEAD")
  val OPTIONS = Method("OPTIONS")
  val PUT = Method("PUT")
  val DELETE = Method("DELETE")
  val TRACE = Method("TRACE")

  object Implicits {
    /** Transform a method string into a [[Method]] instance.
      *
      * Given that this library is very unlikely to be
      * used with other more exotic methods, if the user specifies an unsupported method,
      * it is probably a typo rather than an actual HTTP method. We therefore report the
      * error early as an IllegalArgumentException
      *
      * @param name "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE" or "TRACE"; Case insensitive.
      * @throws IllegalArgumentException when name is none of the legal values.
      * @return The method instance for the provided method name.
      */
    implicit def fromString(name: String): Method = name.toUpperCase match {
      case "GET" => GET
      case "POST" => POST
      case "HEAD" => HEAD
      case "OPTIONS" => OPTIONS
      case "PUT" => PUT
      case "DELETE" => DELETE
      case "TRACE" => TRACE
      case _ => throw new IllegalArgumentException(s"Invalid HTTP method: $name")
    }
  }
}
