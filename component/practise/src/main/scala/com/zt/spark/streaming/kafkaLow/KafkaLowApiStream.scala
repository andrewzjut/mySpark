package com.zt.spark.streaming.kafkaLow

import com.typesafe.scalalogging.LazyLogging
import com.zt.scala.constant.KafkaProperties
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.TaskContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, HasOffsetRanges, KafkaUtils, OffsetRange}
import org.apache.spark.streaming.{Milliseconds, StreamingContext}

object KafkaLowApiStream extends App with LazyLogging {
  val topic1 = "KafkaLowApiStream3"
  val topic2 = "KafkaLowApiStream4"


  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> KafkaProperties.BOOTSTRAPS,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "gateway",
    "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )


  //  val km = new KafkaManager(kafkaParams)


  val spark = SparkSession.builder.master("local[2]").appName("KafkaLowApiStream").getOrCreate()

  val ssc = new StreamingContext(spark.sparkContext, Milliseconds(5000))

  val stream = KafkaUtils.createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String](List(topic1), kafkaParams)
  )

  def dealLine(line: String): String = {
    line.toLowerCase
  }

  def process(record: ConsumerRecord[_, _]): Long = {
    try {
      println(s"key:${record.key()} ,value:${record.value()} , partition:${record.partition()}, offset:${record.offset()}")
      1l
    } catch {
      case e: Throwable => 0l
    }
  }

  stream.foreachRDD(rdd => {
    if (!rdd.isEmpty()) {

      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges

      rdd.foreachPartition { _ =>
        val o: OffsetRange = offsetRanges(TaskContext.get.partitionId)
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      }


      val processedOffsetRanges: RDD[OffsetRange] = rdd.mapPartitions[OffsetRange](records => {
        var offsetRange: OffsetRange = null
        var processedOffset: Long = 0
        records.zipWithIndex.foreach(r => {
          val record = r._1.asInstanceOf[ConsumerRecord[_, _]]

          if (offsetRange == null) {
            offsetRange = offsetRanges.find(_.partition == record.partition()).get
          }
          processedOffset = record.offset()

          val result = process(record)
          if (result == 1l) {
            processedOffset += 1
          } else {
            logger.error("")
          }
        })
        Iterator("").filter(_ => offsetRange != null)
          .map(_ => OffsetRange(offsetRange.topic, offsetRange.partition, offsetRange.fromOffset, processedOffset))
      })
      stream.asInstanceOf[CanCommitOffsets].commitAsync(processedOffsetRanges.collect())

    }
  })

  val kafkaParams2 = Map[String, Object](
    "bootstrap.servers" -> KafkaProperties.BOOTSTRAPS,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "gateway",
    "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (true: java.lang.Boolean)
  )

  //同一个代码里面的多个流 分别处理不同的topic
  val stream2 = KafkaUtils.createDirectStream[String, String](
    ssc,
    PreferConsistent,
    Subscribe[String, String](List(topic2), kafkaParams2)
  )

  stream2.foreachRDD(rdd => {
    rdd.foreach(record => println(record))
  })

  ssc.start()
  ssc.awaitTermination()

}
