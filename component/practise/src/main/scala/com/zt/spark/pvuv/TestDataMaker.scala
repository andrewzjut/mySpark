package com.zt.spark.pvuv

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}
import java.util.{Properties, Random, UUID}

import com.zt.scala.constant.KafkaProperties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
object TestDataMaker {
  val topic: String = "nginx-logs"
  def main(args: Array[String]): Unit = {



    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.BOOTSTRAPS)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "DemoProducer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    val producer = new KafkaProducer[String, String](props)


    val sites = Array(1, 2, 3, 4, 5, 6, 7, 8, 9)
    val pcs = new Array[String](100)
    for (i <- 0 until 100) {
      val pc = UUID.randomUUID().toString.replaceAll("-", "")
      pcs(i) = pc
    }

    val dateformat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    val localDate = LocalDate.now()

    while (true){
      for (i <- 0 to 10000) {

        val userId = new Random().nextInt(100)

        val hour = new Random().nextInt(23)
        val minute = new Random().nextInt(59)
        val second = new Random().nextInt(59)

        val localDateTime: LocalDateTime = LocalDateTime.of(localDate.getYear, localDate.getMonth, localDate.getDayOfMonth, hour, minute, second)
        val log = new StringBuilder
        val time = localDateTime.format(dateformat)
        log.append(time).append("|")
        log.append("200").append("|")
        log.append("/test?")
        log.append("pcid=").append(pcs(userId))
        log.append("&")
        log.append("siteid=").append(sites(userId % 9))
        println(log.toString())
        producer.send(new ProducerRecord[String, String](topic, 0, "", log.toString()))

      }

      Thread.sleep(5000)
    }


  }


}
