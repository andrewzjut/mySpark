package com.zt.myspark.templates

import com.zt.scala.common.StreamRecord
import org.apache.spark.internal.Logging
import org.apache.spark.sql.ForeachWriter
import redis.clients.jedis.Jedis

abstract class RedisForeachWriter[E](address: String, dbIdx: Int = 0, auth: String = null,
                                     maxTotal: Int = 200, maxIdle: Int = 5, maxWaitMillis: Long = 5000, minIdle: Int = 2, connectionTimeout: Int = 2000)
  extends ForeachWriter[StreamRecord[E]] with Logging with Serializable {
  protected var jedis: Jedis = _

  override def open(partitionId: Long, version: Long): Boolean = {
    log.trace(s"[open] partitionId :$partitionId")
    RedisTemplate.init(address, dbIdx, auth, maxTotal, maxWaitMillis, maxIdle, connectionTimeout)
    jedis = RedisTemplate.get()
    openFun(jedis, partitionId, version)
  }

  override def process(value: StreamRecord[E]): Unit = {
    log.trace(s"[process] Record parse :${value.toString}")
    processFun(value, jedis)
  }

  override def close(errorOrNull: Throwable): Unit = {
    log.trace(s"[close] errorOrNull :${if (errorOrNull == null) "" else errorOrNull.getMessage}")
    jedis.close()
    RedisTemplate.close()
    closeFun(errorOrNull)
  }


  def openFun(jedis: Jedis, partitionId: Long, version: Long): Boolean = {
    true
  }

  def processFun(record: StreamRecord[E], jedis: Jedis): Unit

  def closeFun(errorOrNull: Throwable): Unit = {}

}

