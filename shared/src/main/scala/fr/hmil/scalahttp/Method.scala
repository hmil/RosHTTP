package fr.hmil.scalahttp



final class Method private(val name: String)


/**
  * TODO: doc
  */
object Method {
  val OPTIONS = Method("OPTIONS")
  val GET = Method("GET")
  val HEAD = Method("HEAD")
  val POST = Method("POST")
  val PUT = Method("PUT")
  val DELETE = Method("DELETE")
  val TRACE = Method("TRACE")
  val CONNECT = Method("CONNECT")
  implicit def apply(name: String): Method = new Method(name)
}

