package fr.hmil.roshttp.node.http

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

/**
  * For our purposes, we can just pretend https has the same interface as http
  */
@js.native
private[roshttp] trait Https extends Http

@js.native
@JSName("https")
private[roshttp] object Https extends Https
