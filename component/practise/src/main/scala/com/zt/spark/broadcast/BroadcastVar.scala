package com.zt.spark.broadcast

import java.util.Properties

import com.zt.myspark.templates.RedisTemplate
import com.zt.scala.constant.KafkaProperties
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession

object BroadcastVar {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[2]")
      .appName("BroadcastVar")

      .getOrCreate()
    RedisTemplate.init("10.200.150.8:6868")
    RedisTemplate.get().set("a", "zt")

    val data = List(1, 2, 3, 4, 5, 6)
    val bdata: Broadcast[List[Int]] = spark.sparkContext.broadcast(data)


    val rdd = spark.sparkContext.parallelize(Seq(1, 2, 3, 4, 5, 6, 7, 8, 9), 5)

//    rdd.foreachPartition {
//      r =>
//        RedisTemplate.init("10.200.150.8:6868")
//        val jedis: Jedis = RedisTemplate.get()
//        println(jedis.get("a") + " " + jedis.hashCode())
//    }

    val kafkaProducer: Broadcast[KafkaSink[String, String]] = {
      val kafkaProducerConfig = {
        val p = new Properties()
        p.setProperty("bootstrap.servers", KafkaProperties.KAFKA_SERVER_URL)
        p.setProperty("key.serializer", classOf[StringSerializer].getName)
        p.setProperty("value.serializer", classOf[StringSerializer].getName)
        p
      }
      spark.sparkContext.broadcast(KafkaSink[String, String](kafkaProducerConfig))
    }

    val redisClient: Broadcast[RedisClient] = spark.sparkContext.broadcast(RedisClient("10.200.150.8", 6868))

    rdd.foreach(str => {
      kafkaProducer.value.send(KafkaProperties.TOPIC, str.toString)
      val jedis = redisClient.value.getResource()
      try {
        println(jedis.get("a") + " " + jedis.hashCode())
      } finally {
        jedis.close
      }
    })

    rdd.foreachPartition(r => {
      val jedis = redisClient.value.getResource()
      println(jedis.get("a") + " " + jedis.hashCode())
    })

  }
}
