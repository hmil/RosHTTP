package fr.hmil.scalahttp.node

private[scalahttp] abstract class Module[T](val name: String, val inst: T) {
  def isAvailable: Boolean = required.isDefined

  def required: Option[T] = Helpers.require(this)
  lazy val api = required.getOrElse(throw new ModuleNotFoundException(name))
}

private[scalahttp] class ModuleNotFoundException(name: String) extends RuntimeException("Module " + name + " not found")
