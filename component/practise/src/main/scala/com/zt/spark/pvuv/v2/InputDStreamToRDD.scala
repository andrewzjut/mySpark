package com.zt.spark.pvuv.v2

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Durations, StreamingContext}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object InputDStreamToRDD {
  def main(args: Array[String]): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

    val spark = SparkSession
      .builder()
      .master("local[2]")
      .appName("InputDStreamToRDD")
      .config("spark.streaming.kafka.maxRatePerPartition","20000")

      .getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Durations.seconds(10))

    ssc.checkpoint("E:\\git\\study\\SparkDemo\\spark-warehouse")

    //数据源来自KAFKA
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "10.200.159.6:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "user_payment",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array("user_payment")

    //从kafka读入数据并且将json串进行解析
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )

    //    rddDemo1(stream)
    //    rddDemo2(stream)
    //    rddDemo3(stream)
    //    rddDemo4(stream)
    rddDemo5(stream)
    ssc.start()
    ssc.awaitTermination()
  }

  //使用redis缓存基数估计统计uv
  def rddDemo5(stream: InputDStream[ConsumerRecord[String, String]]): Unit = {
    //对一分钟的数据进行计算
    val lines = stream.map(_.value)
    val words = lines.flatMap(_.split(" ")).filter(!_.equals("message:"))
    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)

    //输出
    wordCounts.foreachRDD(x =>
      x.foreachPartition(partition => {
        //保存到Redis中
        val jedis = RedisClient.pool.getResource

        partition.foreach(x => {
          println("id=" + x._1 + " count=" + x._2)
          jedis.pfadd("app::uv::today", x._1)
        })
        jedis.close()
      }
      ))
  }
}
