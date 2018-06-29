package com.zt.vertx.timer.process

import com.typesafe.scalalogging.LazyLogging
import com.zt.vertx.timer.dto.TimerTaskReq
import io.vertx.core.Vertx

import scala.concurrent.Future

trait TimerProcessor extends LazyLogging {
  def init(vertx: Vertx): Unit

  def process(timerTaskReq: TimerTaskReq): Future[Void]
}
