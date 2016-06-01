package fr.hmil.scalahttp

/** Wraps HTTP method strings. */
final class Method private(private val name: String) {

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
  val PATCH = Method("PATCH")
  val TRACE = Method("TRACE")

  def apply(name: String): Method = new Method(name)
}
