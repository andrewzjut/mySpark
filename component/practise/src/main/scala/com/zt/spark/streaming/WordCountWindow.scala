package com.zt.spark.streaming

import java.util.UUID

import org.apache.spark.internal.Logging
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.window

object WordCountWindow extends Logging {
  def main(args: Array[String]): Unit = {
    val checkpointLocation =
      if (args.length > 3) args(3) else "/tmp/temporary-" + UUID.randomUUID.toString

    val spark = SparkSession
      .builder()
      .master("local")
      .appName("wordcount Window")
      .getOrCreate()

    import spark.implicits._

    val lines = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", "9999")
      .load()

    val words = lines.as[String].map(visit => visit.split(" ")).map(tp => (tp(0), tp(1))).toDF("timestamp", "name")
    val animalVisit = words.withColumn("timestamp", $"timestamp".cast("timestamp"))


    animalVisit.printSchema()

    val windowedCounts = animalVisit
      .withWatermark("timestamp", "10 minutes")
      .groupBy(
        window($"timestamp".cast("timestamp"), "10 minutes", "5 minutes"),
        $"name"
      ).count()

    windowedCounts.printSchema()

    //console sink
    val query = windowedCounts
      .writeStream
      .format("console")
      .option("checkpointLocation", checkpointLocation)
      .outputMode("complete")
      .start()

    // file sink
    /*     val query = windowedCounts.repartition(1)
        .writeStream
        .option("checkpointLocation", checkpointLocation)
        .outputMode("complete")
        .foreach(
          new ForeachWriter[Row] {

            var fileWriter: FileWriter = _

            override def process(value: Row): Unit = {
              fileWriter.append(value.toSeq.mkString(","))
              fileWriter.append("\n")
            }

            override def close(errorOrNull: Throwable): Unit = {
              fileWriter.close()
            }

            override def open(partitionId: Long, version: Long): Boolean = {
              FileUtils.forceMkdir(new File(s"/tmp/example/${partitionId}"))
              fileWriter = new FileWriter(new File(s"/tmp/example/${partitionId}/temp"))
              true
            }
          }
        )
        .start()*/

    //自定义kafka
    /*    val writer = new KafkaSink("topic", "localhost:9092")
        val query =
          windowedCounts
            .writeStream
            .foreach(writer)
            .outputMode("update")
            .start()*/
    //kafka format
    /*    val query = windowedCounts
          .writeStream
          .format("kafka")
          .option("kafka.bootstrap.servers", "kafka.nd1.trcloud.com:6667")
          .option("topic", "updates")
          .option("checkpointLocation", checkpointLocation)
          .start()*/

    //file sink 不支持聚合操作
    /*val query =
      animalVisit.writeStream
        .format("json")
        .option("path", "hdfs://192.168.129.102:9000/tmp")
        .outputMode("append")
        .option("checkpointLocation", checkpointLocation)
        .start()*/

    //    val query = windowedCounts
    //      .writeStream
    //      .format("memory")
    //      .queryName("animal")
    //      .outputMode("complete")
    //      .start()
    query.awaitTermination()

  }
}
