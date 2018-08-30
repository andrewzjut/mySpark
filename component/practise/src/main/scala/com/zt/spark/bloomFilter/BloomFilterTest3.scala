package com.zt.spark.bloomFilter

import com.zt.spark.pvuv.InternalRedisClient
import orestes.bloomfilter.{BloomFilter, FilterBuilder}
import redis.clients.jedis.Jedis

object BloomFilterTest3 {
  private var bloomFilter: BloomFilter[String] = _
  private var jedis: Jedis = _
  val name = "zt.uv"


  def init(): Unit = {
    bloomFilter = new FilterBuilder(1000000, 0.0001)
      .name(name + "_bloom")
      .redisBacked(true)
      .redisHost("localhost")
      .redisPort(6379)
      .buildBloomFilter()
  }

  def main(args: Array[String]): Unit = {
    init()
    // Redis configurations
    val maxTotal = 10
    val maxIdle = 10
    val minIdle = 1
    val redisHost = "localhost"
    val redisPort = 6379
    val redisTimeout = 30000
    //默认db，用户存放Offset和pv数据
    val dbDefaultIndex = 0
    InternalRedisClient.makePool(redisHost, redisPort, redisTimeout, maxTotal, maxIdle, minIdle)

    jedis = InternalRedisClient.getPool.getResource


    add("user1", jedis)
    add("user2", jedis)
    add("user3", jedis)
    add("user4", jedis)
    add("user5", jedis)
    add("user6", jedis)
    add("user7", jedis)


    for (i <- 0 until 10) {
      add("user" + 0, jedis)
      timelength("user" + 0, "2018-08-1" + i, jedis)
    }



    println(count(jedis))


  }

  def add(value: String, jedis: Jedis): Boolean = {
    if (bloomFilter != null) {
      if (bloomFilter.contains(value)) {
        false
      } else {
        bloomFilter.add(value)
        jedis.incr(name)
        true
      }
    } else {
      // TODO 不使用bloom的实现
      false
    }
  }

  def count(jedis: Jedis): Long = {
    jedis.get(name).toLong
  }

  def timelength(key: String, value: String, jedis: Jedis): Boolean = {
    if (bloomFilter != null) {
      if (bloomFilter.contains(key)) {
        false
      } else {
        bloomFilter.add(value)
        jedis.incr(name)
        true
      }
    } else {
      // TODO 不使用bloom的实现
      false
    }
  }
}
