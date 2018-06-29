package com.zt.vertx.timer.adapter

import com.typesafe.scalalogging.LazyLogging
import com.zt.vertx.timer.dto.TimerTaskReq
import io.vertx.core.Vertx

import scala.concurrent.Future

trait IOAdapter extends LazyLogging {
  def init(vertx: Vertx): Unit

  def receive(fun: TimerTaskReq => Future[Void]): Unit

  def send(category: String, key: String, value: String): Future[Void]
}
