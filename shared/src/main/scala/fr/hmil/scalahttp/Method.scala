package fr.hmil.scalahttp



final case class Method private (name: String) {

  override implicit def toString: String = name

  override def equals(o: Any): Boolean = o match {
    case that: Method => that.name.equalsIgnoreCase(this.name)
    case _ => false
  }

  override def hashCode: Int = name.toUpperCase.hashCode
}


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
  implicit def fromString(name: String): Method = new Method(name)
}

