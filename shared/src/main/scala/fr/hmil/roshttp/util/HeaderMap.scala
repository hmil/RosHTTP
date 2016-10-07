package fr.hmil.roshttp.util

import fr.hmil.roshttp.util.HeaderMap.CaseInsensitiveString

import scala.collection.immutable.MapLike


/** A set of HTTP headers identified by case insensitive keys
  *
  * A Map[CaseInsensitiveString, String] would conform to the strict Map specification
  * but it would make the API ugly, forcing the explicit usage of CaseInsensitiveString
  * instead of string.
  *
  * That's why we have the HeaderMap class to represent HTTP headers in a map like
  * interface which is nice to use. It is however not *exactly* a map because
  * different keys can map to the same value if they are case-insensitive equivalent.
  *
  * @tparam B Required for MapLike implementation. Should always be set to String.
  */
class HeaderMap[B >: String] private(map: Map[CaseInsensitiveString, B] = Map())
  extends Map[String, B]
  with MapLike[String, B, HeaderMap[B]] {

  override def empty: HeaderMap[B] = new HeaderMap(Map())

  override def get(key: String): Option[B] = {
    map.get(new CaseInsensitiveString(key))
  }

  override def iterator: Iterator[(String, B)] = {
    map.map({ t => (t._1.value, t._2)}).iterator
  }

  override def +[B1 >: B](kv: (String, B1)): HeaderMap[B1] = {
    val key = new CaseInsensitiveString(kv._1)
    new HeaderMap[B1](map - key + (key -> kv._2))
  }

  override def -(key: String): HeaderMap[B] = {
    new HeaderMap[B](map - new CaseInsensitiveString(key))
  }

  override def toString: String = {
    map.map({ t => t._1 + ": " + t._2}).mkString("\n")
  }
}

object HeaderMap {

  /** Creates a HeaderMap from a map of string to string. */
  def apply(map: Map[String, String]): HeaderMap[String] = new HeaderMap(
    map.map(t => (new CaseInsensitiveString(t._1), t._2))
  )

  /** Creates an empty HeaderMap. */
  def apply(): HeaderMap[String] = HeaderMap(Map())

  /** A string whose equals and hashCode methods are case insensitive. */
  class CaseInsensitiveString(val value: String) {

    override def equals(other: Any): Boolean = other match {
      case s:CaseInsensitiveString => s.value.equalsIgnoreCase(value)
      case _ => false
    }

    override def hashCode(): Int = value.toLowerCase.hashCode

    override def toString: String = value
  }
}