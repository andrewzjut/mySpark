package com.zt.spark.streaming

import java.sql.Timestamp
import java.util.UUID

import com.zt.myspark.templates.RedisTemplate
import com.zt.scala.common.{StreamRecord, Visit}
import com.zt.scala.utils.JsonHelper
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.internal.Logging
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}


/*

{"referer":"www.baidu.com","url":"www.tairanmall.com","userFlag":"1","ip":"192.168.129.101","time":1528096634000}
{"referer":"www.baidu.com","url":"www.tairanmall.com/login","userFlag":"1","ip":"192.168.129.101","time":1528096635000}
bin/kafka-console-producer --broker-list 10.200.131.154:9092 --topic StructuredKafka2
bin/kafka-console-consumer --bootstrap-server 10.200.131.154:9092 --topic StructuredKafka2  --from-beginning

 */
object StructuredKafka2 extends Logging {
  def main(args: Array[String]): Unit = {
    val checkpointLocation =
      if (args.length > 3) args(3) else "/tmp/temporary-" + UUID.randomUUID.toString

    val fromOffsets = Map {
      new TopicPartition("StructuredKafka2", 0) -> 0l
    }


    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "10.200.131.154:9092,10.200.131.155:9092,10.200.131.156:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "exactly-once",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "auto.offset.reset" -> "latest"
    )

    val conf = new SparkConf().setAppName("TestSparkStreaming").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(10))

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      Subscribe[String, String](Seq("StructuredKafka2"), kafkaParams)
      // ConsumerStrategies.Assign[String, String](fromOffsets.keys.toList, kafkaParams, fromOffsets)
    )


    stream.foreachRDD {
      rdd =>
        rdd.mapPartitions {
          r =>
            r.map {
              record =>
                val value = record.value()
                val visit = JsonHelper.toObject[Visit](value)
                StreamRecord[Visit](record.topic(), new Timestamp(record.timestamp()), visit)
            }.filter {
              record =>
                try {
                  record.body.ip != "127.0.0.1" && record.body.ip != "localhost"
                } catch {
                  case e: Throwable =>
                    log.warn(s"Record parse error:${record.toString}", e)
                    false
                }
            }
        }.repartition(10).foreachPartition {
          RedisTemplate.init("10.200.150.8:6868")
          rdd =>
            rdd.foreach { streamRecord =>
              println(streamRecord.body)
              val jedis = RedisTemplate.get()
              try {
                jedis.incr("ip:" + streamRecord.body.ip)
                //...
              } finally {
                jedis.close()
              }
            }
        }
    }
    ssc.start()
    ssc.awaitTermination()
  }
}
