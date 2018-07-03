package com.zt.vertx.timer

import com.typesafe.scalalogging.LazyLogging
import com.zt.scala.vertx.VertxTemplate
import com.zt.vertx.timer.adapter.IOAdapter
import com.zt.vertx.timer.process.TimerProcessor
import io.vertx.core.VertxOptions
import javax.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TimerInitiator @Autowired()(timerConfig: TimerConfig,
                                  iOAdapter: IOAdapter,
                                  timerProcessor: TimerProcessor) extends LazyLogging {

  @PostConstruct
  def init(): Unit = {
    val vertx = VertxTemplate(new VertxOptions()
      .setEventLoopPoolSize(timerConfig.vertx.eventLoopPoolSize)
      .setWorkerPoolSize(timerConfig.vertx.workerPoolSize)
      .setInternalBlockingPoolSize(timerConfig.vertx.internalBlockingPoolSize)
      .setMaxWorkerExecuteTime(timerConfig.vertx.workerPoolSize)
      .setFileResolverCachingEnabled(false))
      .vertx
    iOAdapter.init(vertx)
    timerProcessor.init(vertx)
  }
}
