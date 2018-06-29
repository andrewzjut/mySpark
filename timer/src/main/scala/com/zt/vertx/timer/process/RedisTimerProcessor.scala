package com.zt.vertx.timer.process

import java.util.concurrent.Executors

import com.tairanchina.csp.dew.Dew
import com.zt.scala.vertx.{AsyncKafkaTemplate, AsyncRedisTemplate}
import com.zt.vertx.timer.TimerConfig
import com.zt.vertx.timer.adapter.IOAdapter
import com.zt.vertx.timer.dto.TimerTaskReq
import io.vertx.core.Vertx
import io.vertx.redis.RedisOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.collection.mutable
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable, Future, Promise}

@Component
class RedisTimerProcessor @Autowired()(timerConfig: TimerConfig,
                                       iOAdapter: IOAdapter) extends TimerProcessor {

  private var redis: AsyncRedisTemplate = _
  private val executor = Executors.newCachedThreadPool()


  override def init(vertx: Vertx): Unit = {
    redis = AsyncRedisTemplate(
      new RedisOptions()
        .setHost(timerConfig.redis.host)
        .setPort(timerConfig.redis.port)
        .setAuth(timerConfig.redis.auth)
        .setSelect(timerConfig.redis.db)
        .setUsePooledBuffers(true).asInstanceOf[RedisOptions], vertx)

    redis.hkeys(RedisTimerProcessor.REDIS_STORAGE_CATEGORIES).onSuccess {
      case categories =>
        categories.foreach(category => {
          redis.hlen(RedisTimerProcessor.REDIS_STORAGE_MAP + category).onSuccess {
            case len =>
              if (len > 0) {
                startConsumeTask(category)
              } else {
                redis.hdel(RedisTimerProcessor.REDIS_STORAGE_CATEGORIES, category)
              }
          }
        })
    }
    iOAdapter.receive(req => process(req))
  }

  def startConsumeTask(category: String): Unit = {
    executor.execute(() => {
      while (true) {
        if (Dew.cluster.election == null || Dew.cluster.election.isLeader) {
          Await.result(consumeTask(category), Duration.Inf)
        } else {
          Thread.sleep(1000L)
        }
      }
    })
  }

  override def process(timerTaskReq: TimerTaskReq): Future[Void] = {

    val promise = Promise[Void]

    redis.hexists(RedisTimerProcessor.REDIS_STORAGE_CATEGORIES, timerTaskReq.cate).onSuccess {
      case exist =>
        if (!exist) {
          redis.hset(RedisTimerProcessor.REDIS_STORAGE_CATEGORIES, timerTaskReq.cate, "")
          if (!RedisTimerProcessor.categories.contains(timerTaskReq.cate)) {
            RedisTimerProcessor.categories += timerTaskReq.cate
            startConsumeTask(timerTaskReq.cate)
          }
        }
    }

    promise.future
  }

  private def consumeTask(category: String): Future[Void] = ???

}

object RedisTimerProcessor {

  @volatile var categories: mutable.Set[String] = new mutable.HashSet[String]
  private val REDIS_STORAGE_CATEGORIES = "dmp:timer:storage:categories"
  private val REDIS_STORAGE_QUEUE = "dmp:timer:storage:queue:"
  private val REDIS_STORAGE_MAP = "dmp:timer:storage:map:"

}
