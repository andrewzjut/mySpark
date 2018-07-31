package com.zt.spark.pvuv

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, HasOffsetRanges, KafkaUtils, LocationStrategies}
import redis.clients.jedis.Pipeline

case class MyRecord(day:String,hour: String, user_id: String, site_id: String)

object TestSparkStreaming {
  def main(args: Array[String]): Unit = {

    val brokers = "10.200.20.98:9092"
    val topic = TestDataMaker.topic
    val partition: Int = 0 //测试topic只有一个分区

    //Kafka参数
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> brokers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "exactly-once",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "auto.offset.reset" -> "none"
    )

    // Redis configurations
    val maxTotal = 10
    val maxIdle = 10
    val minIdle = 1
    val redisHost = "10.200.150.8"
    val redisPort = 6868
    val redisTimeout = 30000
    //默认db，用户存放Offset和pv数据
    val dbDefaultIndex = 8
    InternalRedisClient.makePool(redisHost, redisPort, redisTimeout, maxTotal, maxIdle, minIdle)


    val spark = SparkSession.builder()
      .appName("TestSparkStreaming")
//      .master("local[2]")
      .config("spark.streaming.kafka.maxRatePerPartition", "20000")
      .getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Seconds(10))

    //从Redis获取上一次存的Offset
    val jedis = InternalRedisClient.getPool.getResource
    jedis.select(dbDefaultIndex)
    val topic_partition_key = topic + "_" + partition
    var lastOffset = 0l
    val lastSavedOffset = jedis.get(topic_partition_key)

    if (null != lastSavedOffset) {
      try {
        lastOffset = lastSavedOffset.toLong
      } catch {
        case ex: Exception => println(ex.getMessage)
          println("get lastSavedOffset error, lastSavedOffset from redis [" + lastSavedOffset + "] ")
          System.exit(1)
      }
    }
    InternalRedisClient.getPool.returnResource(jedis)

    println("lastOffset from redis -> " + lastOffset)

    //设置每个分区起始的Offset
    val fromOffsets = Map {
      new TopicPartition(topic, partition) -> lastOffset
    }

    //使用Direct API 创建Stream
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Assign[String, String](fromOffsets.keys.toList, kafkaParams, fromOffsets)
    )

    //开始处理批次消息
    stream.foreachRDD {
      rdd =>
        val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

        val result = processLogs(rdd)
        println("=============== Total " + result.length + " events in this batch ..")

        val jedis = InternalRedisClient.getPool.getResource
        val p1: Pipeline = jedis.pipelined();
        p1.select(dbDefaultIndex)
        p1.multi() //开启事务


        //逐条处理消息
        result.foreach {
          record =>
            //增加小时总pv
            val pv_by_hour_key = "pv:" + record.day + ":" + record.hour
            p1.incr(pv_by_hour_key)

            //增加网站小时pv
            val site_pv_by_hour_key = "site:pv:" + record.site_id + ":" + record.day + ":" + record.hour
            p1.incr(site_pv_by_hour_key)

            //使用set保存当天的uv
            val uv_by_day_key = "uv:" + record.day
//            p1.sadd(uv_by_day_key + "_1", record.user_id) //set 存储
            p1.pfadd(uv_by_day_key, record.user_id)         //技术估计
        }

        //更新Offset
        offsetRanges.foreach { offsetRange =>
          println("partition : " + offsetRange.partition + " fromOffset:  " + offsetRange.fromOffset + " untilOffset: " + offsetRange.untilOffset)
          val topic_partition_key = offsetRange.topic + "_" + offsetRange.partition
          p1.set(topic_partition_key, offsetRange.untilOffset + "")
        }

        p1.exec(); //提交事务
        p1.sync(); //关闭pipeline

        InternalRedisClient.getPool.returnResource(jedis)

    }


    def processLogs(messages: RDD[ConsumerRecord[String, String]]): Array[MyRecord] = {
      messages.map(_.value()).flatMap(parseLog).collect()
    }

    //解析每条日志，生成MyRecord
    def parseLog(line: String): Option[MyRecord] = {
      val ary: Array[String] = line.split("\\|", -1);
      try {
        val day = ary(0).substring(0, 10)
        val hour = ary(0).substring(11, 13)
        val uri = ary(2).split("[=|&]", -1)
        val user_id = uri(1)
        val site_id = uri(3)
        return Some(MyRecord(day,hour, user_id, site_id))

      } catch {
        case ex: Exception => println(ex.getMessage)
      }

      return None

    }


    ssc.start()
    ssc.awaitTermination()
  }
}
