package com.zt.scala.kafka.string

import java.util.Properties
import java.util.concurrent.Future

import com.zt.scala.constant.KafkaProperties
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

class Producer(val pTopic: String, pIsAsync: Boolean) extends Thread {

  private var kafkaProducer: KafkaProducer[String, String] = _
  private var topic: String = pTopic
  private var isAsync: Boolean = pIsAsync

  val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.BOOTSTRAPS)
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "DemoProducer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  val producer = new KafkaProducer[String, String](props)

  override def run(): Unit = {
    var messageNo: Int = 1
    while (messageNo <= 100) {
      val messageStr = String.valueOf(System.currentTimeMillis())

      val bytes = new Array[Byte](1024*1000)
      for (i <- 0 to bytes.length - 1) {
        bytes(i) = 'a'
      }

      println(topic + " " + isAsync)

      val startTime = System.currentTimeMillis()
      if (isAsync) {
        producer.send(new ProducerRecord[String, String](
          topic,
          new String(bytes)), new MyCallBack(startTime, messageNo, messageStr))
      } else {

        try {
          val future: Future[RecordMetadata] = producer.send(
            new ProducerRecord(topic, messageStr))
          val recordMetadata = future.get()
          System.out.println("Sent message: (" + messageNo + ", " + messageStr + ") to partition: "
            + recordMetadata.partition() + " , offset: " + recordMetadata.offset());
        } catch {
          case e: Throwable => throw e
        }
      }
      messageNo += 1
    }
  }


}

class MyCallBack(sTime: Long, k: Int, msg: String) extends Callback {

  private val startTime: Long = sTime
  private val key: Int = k
  private val message: String = msg


  override def onCompletion(metadata: RecordMetadata, e: Exception): Unit = {
    val elapsedTime = System.currentTimeMillis() - startTime
    if (metadata != null) {
      println("message(" + key + ", " + message + ") sent to partition(" + metadata.partition() +
        "), " +
        "offset(" + metadata.offset() + ") in " + elapsedTime + " ms")
    } else {
      e.printStackTrace()
    }
  }
}

object Te extends App {
  val producer = new Producer("test3", true)
  producer.start()
}