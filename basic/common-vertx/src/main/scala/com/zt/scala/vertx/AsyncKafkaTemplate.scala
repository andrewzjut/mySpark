package com.zt.scala.vertx

import java.util

import com.typesafe.scalalogging.LazyLogging
import io.vertx.core.{AsyncResult, Handler, Vertx, VertxOptions}
import io.vertx.kafka.client.consumer.{KafkaConsumer, KafkaConsumerRecord}
import io.vertx.kafka.client.producer.{KafkaProducer, KafkaProducerRecord, RecordMetadata}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

case class AsyncKafkaTemplate(servers: String, groupId: String,
                              autoCommit: Boolean = false, reset: String = "earliest", acks: Int = 1,
                              vertx: Vertx = VertxTemplate(new VertxOptions()).vertx) extends LazyLogging with Serializable {

  private val kafkaConsumer = KafkaConsumer.create[String, String](vertx, new util.HashMap[String, String]() {
    {
      put("bootstrap.servers", servers)
      put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
      put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
      put("group.id", groupId)
      put("auto.offset.reset", reset)
      put("enable.auto.commit", autoCommit.toString)
    }
  })

  private val kafkaProducer = KafkaProducer.create[String, String](vertx, new util.HashMap[String, String]() {
    {
      put("bootstrap.servers", servers)
      put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      put("acks", acks + "")
    }
  })

  logger.info("Vertx Kafka Server Start at {} - {}", servers, groupId)

  sys.addShutdownHook {
    kafkaConsumer.unsubscribe()
    kafkaProducer.close()
  }

  def receive(topics: Set[String], fun: ReceiveRequest => Future[Void]): Unit = {
    logger.info("Init listening...")
    kafkaConsumer.handler(new Handler[KafkaConsumerRecord[String, String]]() {
      override def handle(record: KafkaConsumerRecord[String, String]): Unit = {
        try {
          fun(ReceiveRequest(record.topic(), record.key(), record.value())).onComplete {
            case Success(_) =>
              if (!autoCommit) {
                kafkaConsumer.commit()
              }
            case Failure(ex) =>
              logger.error(s"Process error : [${record.key()}] ${record.value()}", ex)
          }
        } catch {
          case e: Throwable =>
            logger.warn(s"Input data format error : [${record.key()}] ${record.value()}", e)
            if (!autoCommit) {
              kafkaConsumer.commit()
            }
        }
      }
    })
    kafkaConsumer.subscribe(topics, new Handler[AsyncResult[Void]] {
      override def handle(e: AsyncResult[Void]): Unit = {
        if (e.failed()) {
          logger.error("Could not subscribe " + e.cause.getMessage)
        }
      }
    })
  }

  def send(topic: String, key: String, message: String): Future[Void] = {
    val promise = Promise[Void]
    kafkaProducer.write(
      KafkaProducerRecord.create(topic, key, message),
      new Handler[AsyncResult[RecordMetadata]]() {
        override def handle(e: AsyncResult[RecordMetadata]): Unit = {
          if (e.succeeded()) {
            promise.success(null)
          } else {
            promise.failure(e.cause())
          }
        }
      }
    )
    promise.future
  }

  def getRawProducer: KafkaProducer[String, String] = kafkaProducer

  def getRawConsumer: KafkaConsumer[String, String] = kafkaConsumer

  case class ReceiveRequest(topic: String, key: String, message: String)

}
