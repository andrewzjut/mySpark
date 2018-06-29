package com.zt.vertx.timer.dto

import scala.beans.BeanProperty

case class TimerTaskReq() {
  @BeanProperty
  var cate: String = _
  @BeanProperty
  var key: String = _
  @BeanProperty
  var value: String = _
  @BeanProperty
  var execMs: Long = _
}
