package com.zt.myspark.templates

import org.apache.spark.internal.Logging
import orestes.bloomfilter.{BloomFilter, FilterBuilder}

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

object RedisTemplate extends Logging with Serializable {
  private var instance: JedisPool = _
  private var defaultDBIdx: Int = _
  private var redisAddress: String = _

  def init(address: String, dbIdx: Int = 0, auth: String = null,
           maxTotal: Int = 200, maxWaitMillis: Long = 5000, minIdle: Int = 8, connectionTimeout: Int = 2000): Unit = {
    redisAddress = address
    defaultDBIdx = dbIdx
    if (instance == null) {
      log.info(s"Initialization Redis pool at $address")
      val config = new JedisPoolConfig()
      config.setMaxIdle(minIdle)
      config.setMinIdle(minIdle)
      config.setMaxWaitMillis(maxTotal)
      config.setMaxWaitMillis(maxWaitMillis)
      instance = new JedisPool(config, address.split(":")(0), address.split(":")(1).toInt, connectionTimeout, auth)
    }
  }

  def close(): Unit = instance.close()

  sys.addShutdownHook(() -> close())

  def get(db: Int = defaultDBIdx): Jedis = {
    val jedis = instance.getResource
    jedis.select(db)
    jedis
  }

  case class Distinct(name: String, defaultJedis: Jedis = null) extends Logging {
    private var bloomFilter: BloomFilter[String] = _

    def useBloom(bloomExpectedElements: Int = 0, bloomFalsePositiveProbability: Double = 0.001): Distinct = {
      bloomFilter = new FilterBuilder(bloomExpectedElements, bloomFalsePositiveProbability)
        .name(name + "_bloom")
        .redisBacked(true)
        .redisHost(redisAddress.split(":")(0))
        .redisPort(redisAddress.split(":")(1).toInt)
        .buildBloomFilter()
      this
    }

    def add(value: String, jedis: Jedis = defaultJedis): Boolean = {
      if (bloomFilter != null) {
        if (bloomFilter.contains(value)) {
          false
        } else {
          bloomFilter.add(value)
          jedis.incr(name)
          true
        }
      } else {
        false
      }
    }
    def count(jedis: Jedis = defaultJedis):Long = {
      jedis.get(name).toLong
    }
    def timelength(key:String,value:String,jedis: Jedis=defaultJedis):Boolean={
      if (bloomFilter!=null){
        if (bloomFilter.contains(key)){
          false
        }else{
          bloomFilter.add(value)
          jedis.incr(name)
          true
        }
      }else{
        false
      }
    }
  }

}
