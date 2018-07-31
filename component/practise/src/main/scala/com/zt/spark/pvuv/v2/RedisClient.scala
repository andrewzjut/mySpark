package com.zt.spark.pvuv.v2

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.JedisPool

object RedisClient extends Serializable {

  val redisHost = "localhost"
  val redisPort = 6479
  val redisTimeout = 30000
  val password = "22pBD7.dubbo"
  val database = 0

  lazy val pool = new JedisPool(new GenericObjectPoolConfig(), redisHost, redisPort, redisTimeout, null, database)

  lazy val hook = new Thread {
    override def run = {
      println("Execute hook thread: " + this)
      pool.destroy()
    }
  }
  sys.addShutdownHook(hook.run)


  def main(args: Array[String]): Unit = {
    val dbIndex = 0

    val jedis = RedisClient.pool.getResource
    jedis.select(dbIndex)
    jedis.set("test", "1")
    println(jedis.get("test"))
    //    RedisClient.pool.close()
    RedisClient.pool.returnResource(jedis)
  }
}
