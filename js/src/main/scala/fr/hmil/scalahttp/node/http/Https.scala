package fr.hmil.scalahttp.node.http

import scala.scalajs.js
import scala.scalajs.js.annotation.JSName

/**
  * For our purposes, we can just pretend https has the same interface as http
  */
@js.native
trait Https extends Http

@js.native
@JSName("https")
object Https extends Https
