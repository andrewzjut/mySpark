package com.zt.spark.broadcast

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

class RedisClient(createRedisPool: () => JedisPool) extends Serializable {

  lazy val jedisPool: JedisPool = createRedisPool()

  def getResource(): Jedis = jedisPool.getResource

}

object RedisClient {
  def apply(host: String, port: Int = 6379, dbIdx: Int = 0): RedisClient = {
    val createRedisPool = () => {
      val jedisPoolConfig: JedisPoolConfig = new JedisPoolConfig
      val jedisPool = new JedisPool(jedisPoolConfig, host, port)
      sys.addShutdownHook {
        jedisPool.close()
      }
      jedisPool
    }
    apply(createRedisPool)
  }

  def apply(createRedisPool: () => JedisPool): RedisClient = new RedisClient(createRedisPool)
}
