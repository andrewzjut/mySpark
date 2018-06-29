package com.zt.vertx.timer

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

import scala.beans.BeanProperty


@Configuration
@ConfigurationProperties(prefix = "dmp.component.timer")
class TimerConfig {
  @BeanProperty
  var basic = new BasicConfig()
  @BeanProperty
  var vertx = new VertxConfig()
  @BeanProperty
  var redis = new RedisConfig()
  @BeanProperty
  var kafka = new KafkaConfig()


  class BasicConfig{
    @BeanProperty
    var inputTopic:String = "dmp.timer.input"
    @BeanProperty
    var outputTopicPrefix:String = "dmp.time.output."
  }
  class VertxConfig{
    @BeanProperty
    var eventLoopPoolSize = 4
    @BeanProperty
    var workerPoolSize = 100;
    @BeanProperty
    var internalBlockingPoolSize = 100
    @BeanProperty
    var maxWorkerExecuteTime = 20000000000L
  }
  class RedisConfig{
    @BeanProperty
    var host:String = _
    @BeanProperty
    var port:Int = 6379
    @BeanProperty
    var auth:String = _
    @BeanProperty
    var db:Int = 0
  }
  class KafkaConfig {
    @BeanProperty
    var servers:String = _
    @BeanProperty
    var groupId:String = "dmp_timer"
  }
}
