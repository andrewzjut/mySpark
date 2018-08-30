package com.zt.scala.constant

object KafkaProperties {
  val TOPIC = "testMongodb.yes_db.no_users"
  val KAFKA_SERVER_URL = "10.200.131.154:9092,10.200.131.155:9092,10.200.131.156:9092"
  val KAFKA_SERVER_URL_PRD = "10.210.6.54:9092,10.210.6.55:9092,10.210.6.56:9092"
  val schema_url = "http://10.200.131.154:8081"
  val KAFKA_SERVER_PORT = 9092
  val BOOTSTRAPS = "10.200.20.98:9092,10.200.20.98:9093,10.200.20.98:9094"
  val KAFKA_PRODUCER_BUFFER_SIZE: Int = 64 * 1024
  val CONNECTION_TIMEOUT = 100000
  val TOPIC2 = "topic2"
  val TOPIC3 = "topic3"
  val CLIENT_ID = "SimpleConsumerDemoClient"
}
