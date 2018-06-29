package com.zt.scala.vertx

import com.typesafe.scalalogging.LazyLogging
import io.vertx.core.{Vertx, VertxOptions}

case class VertxTemplate(vertxOptions: VertxOptions) extends LazyLogging with Serializable {

  logger.info("Init Vertx...")

  private val _vertx = Vertx.vertx(vertxOptions)

  def vertx: Vertx = {
    _vertx
  }

}
