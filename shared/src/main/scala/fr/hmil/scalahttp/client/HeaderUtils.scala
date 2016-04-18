package fr.hmil.scalahttp.client

object HeaderUtils {

  object CaseInsensitiveOrder extends Ordering[String] {
    def compare(x: String, y: String): Int =
      x.compareToIgnoreCase(y)
  }

  class CaseInsensitiveString(private val value: String) {

    override def equals(other: Any): Boolean = other match {
      case s:CaseInsensitiveString => s.value.equalsIgnoreCase(value)
      case _ => false
    }

    override def hashCode(): Int = value.toLowerCase.hashCode

    override def toString: String = value
  }

  object CaseInsensitiveString {
    implicit def fromString(s: String): CaseInsensitiveString = new CaseInsensitiveString(s)
  }
}
