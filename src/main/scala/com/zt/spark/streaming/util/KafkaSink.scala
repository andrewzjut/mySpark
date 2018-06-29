package com.zt.spark.streaming.util

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.spark.sql.{ForeachWriter, Row}

class KafkaSink(topic: String, servers: String) extends ForeachWriter[Row] {
  val kafkaProperties = new Properties()
  kafkaProperties.put("bootstrap.servers", servers)
  kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  val results = new scala.collection.mutable.HashMap[String, String]
  var producer: KafkaProducer[String, String] = _

  def open(partitionId: Long, version: Long): Boolean = {
    producer = new KafkaProducer(kafkaProperties)
    true
  }

  def process(value:Row): Unit = {
    producer.send(new ProducerRecord(topic, value.toSeq.mkString(",")))
  }

  def close(errorOrNull: Throwable): Unit = {
    producer.close()
  }
}
object KafkaSink{
  def main(args: Array[String]): Unit = {
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", "kafka.nd1.trcloud.com:6667")
    kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    val producer: KafkaProducer[String, String] = new KafkaProducer(kafkaProperties)
    producer.send(new ProducerRecord("topic", "dd"))

  }
}