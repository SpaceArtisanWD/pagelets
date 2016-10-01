package org.splink.raven

import play.api.{Configuration, Environment}
import play.api.inject.Module

class PageletModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = Seq(
    bind[PageletController].to[Assembly]
  )
}