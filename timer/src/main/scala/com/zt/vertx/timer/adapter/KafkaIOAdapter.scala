package com.zt.vertx.timer.adapter

import com.zt.scala.utils.JsonHelper
import com.zt.scala.vertx.AsyncKafkaTemplate
import com.zt.vertx.timer.TimerConfig
import com.zt.vertx.timer.dto.TimerTaskReq
import io.vertx.core.Vertx
import org.springframework.beans.factory.annotation.Autowired

import scala.concurrent.Future

class KafkaIOAdapter @Autowired()(timerConfig: TimerConfig) extends IOAdapter {


  private var kafka: AsyncKafkaTemplate = _

  override def init(vertx: Vertx): Unit = {
    kafka = AsyncKafkaTemplate(
      servers = timerConfig.kafka.servers,
      groupId = timerConfig.kafka.groupId,
      vertx = vertx)
  }

  override def receive(fun: TimerTaskReq => Future[Void]): Unit = {
    kafka.receive(Set(timerConfig.basic.inputTopic), request => {
      val timerTaskReq = JsonHelper.toObject[TimerTaskReq](request.message)
      timerTaskReq.cate = request.key
      fun(timerTaskReq)
    })
  }

  override def send(category: String, key: String, value: String): Future[Void] = {
    kafka.send(timerConfig.basic.outputTopicPrefix + category, key, value)
  }
}
