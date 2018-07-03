package com.zt.scala.kafka

import java.util
import java.util.{Collections, Properties}

import com.zt.kafka.basic.KafkaProperties
import io.confluent.kafka.serializers.{KafkaAvroDeserializer, KafkaAvroSerializer}
import org.apache.avro.Schema
import org.apache.avro.generic.{GenericData, GenericRecord}
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.internal.Logging

import scala.collection.JavaConversions._

object AvroKafka extends App with Logging {

  //  produce()

  //  prdConsume()
  consume()

  def produce(): Unit = {
    val props = new Properties
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_SERVER_URL)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[KafkaAvroSerializer])
    props.put("schema.registry.url", KafkaProperties.schema_url)
    val producer = new KafkaProducer[String, GenericRecord](props)

    val key = "key1"
    val userSchema = "{\"type\":\"record\"," + "\"name\":\"myrecord\"," + "\"fields\":[{\"name\":\"f1\",\"type\":\"string\"}]}"
    val parser = new Schema.Parser
    val schema = parser.parse(userSchema)
    val avroRecord = new GenericData.Record(schema)
    avroRecord.put("f1", "value1")

    val record = new ProducerRecord[String, GenericRecord]("topic2", 0, key, avroRecord)
    producer.send(record)
  }

  def prdConsume(): Unit = {

    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_SERVER_URL_PRD)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "ddd")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    //    props.put("schema.registry.url", "http://" + KafkaProperties.KAFKA_SERVER_URL + ":8091")
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    val topic1 = "dmp.gateway.source-fdn-t_app_user"
    val topic2 = "dmp.gateway.source-fdn-t_party_person"
    val topic3 = "dmp.gateway.source-fdn-t_prop_app"
    val topic4 = "dmp.gateway.source-fdn-t_prop_ident"
    val topic5 = "dmp.gateway.source-fdn-t_rel_party_prop"
    val topic6 = "dmp.gateway.source-fduser-sys_user"
    val topic7 = "dmp.gateway.source-fduser-user_auth"
    val consumer = new KafkaConsumer[String, String](props)
    val topicPartition = new TopicPartition(topic5, Math.abs(topic5.hashCode % 5))
    consumer.assign(util.Arrays.asList(topicPartition))
    consumer.seek(topicPartition, 0l)

    try {
      val records = consumer.poll(1000)
      for (record <- records) {
        printf("offset = %d, key = %s, value = %s \n", record.offset, record.key, record.value)
      }
    } finally consumer.close()
  }

  def consume(): Unit = {


    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.KAFKA_SERVER_URL)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "ddd-12")
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    props.put("schema.registry.url", KafkaProperties.schema_url)
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getName)
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[KafkaAvroDeserializer].getName)
    val topic = "ucSource1.ucenter_1.1.0.t_app_user";
    val consumer = new KafkaConsumer[String, GenericRecord](props)
    //        val topicPartition = new TopicPartition(topic, 0)
    //        consumer.assign(util.Arrays.asList(topicPartition))
    //        consumer.seek(topicPartition, 0l)
    consumer.subscribe(Collections.singletonList(topic))

    while (true) {
      try {
        val records = consumer.poll(1000)

        for (record <- records) {
          printf("offset = %d, key = %s, value = %s \n", record.offset, record.key, record.value)
          println()
        }

      }
    }
  }
}
