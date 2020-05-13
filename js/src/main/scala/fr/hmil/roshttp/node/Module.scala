package fr.hmil.roshttp.node

private[roshttp] abstract class Module[T](val name: String) {
  def isAvailable: Boolean = require.isDefined

  def require(): Option[T]

  lazy val api = require.getOrElse(throw new ModuleNotFoundException(name))
}

private[roshttp] class ModuleNotFoundException(name: String) extends RuntimeException("Module " + name + " not found")
