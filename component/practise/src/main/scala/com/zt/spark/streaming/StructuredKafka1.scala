package com.zt.spark.streaming

import java.util.UUID

import com.zt.scala.common.{StreamRecord, Visit}
import com.zt.scala.constant.KafkaProperties
import org.apache.spark.internal.Logging
import org.apache.spark.sql.functions.{col, date_format, from_json, get_json_object}
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.{Encoders, SparkSession}

object StructuredKafka1 extends Logging {
  def main(args: Array[String]): Unit = {
    val checkpointLocation =
      if (args.length > 3) args(3) else "/tmp/temporary-" + UUID.randomUUID.toString

    val spark = SparkSession
      .builder()
      .master("local")
      .appName("StructuredKafka1")
      .getOrCreate()

    import spark.implicits._
    val lines = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", KafkaProperties.BOOTSTRAPS)
      .option("subscribe", "StructuredKafka1")
      .option("startingOffsets", "earliest")
      .load()

    val schema = Encoders.product[Visit].schema
    schema.printTreeString()
    /*
    root
     |-- referer: string (nullable = true)
     |-- url: string (nullable = true)
     |-- userFlag: string (nullable = true)
     |-- ip: string (nullable = true)
     |-- time: long (nullable = false)
    */
    lines.printSchema()
    /*
    root
     |-- key: binary (nullable = true)
     |-- value: binary (nullable = true)
     |-- topic: string (nullable = true)
     |-- partition: integer (nullable = true)
     |-- offset: long (nullable = true)
     |-- timestamp: timestamp (nullable = true)
     |-- timestampType: integer (nullable = true)
    */
    val ds = lines.withColumn("body", from_json(col("value").cast(StringType), schema)).as[StreamRecord[Visit]]
    ds.printSchema()
    /*
    root
     |-- key: binary (nullable = true)
     |-- value: binary (nullable = true)
     |-- topic: string (nullable = true)
     |-- partition: integer (nullable = true)
     |-- offset: long (nullable = true)
     |-- timestamp: timestamp (nullable = true)
     |-- timestampType: integer (nullable = true)
     |-- body: struct (nullable = true)
     |    |-- referer: string (nullable = true)
     |    |-- url: string (nullable = true)
     |    |-- userFlag: string (nullable = true)
     |    |-- ip: string (nullable = true)
     |    |-- time: long (nullable = false)
    */
    val dsCorrect = ds.filter {
      record =>
        try {
          record.body.ip != "127.0.0.1" && record.body.ip != "localhost"
        } catch {
          case e: Throwable =>
            log.warn(s"Record parse error:${record.toString}", e)
            false
        }
    }

    val ds2 = lines.withColumn("body", col("value").cast(StringType)).as[StreamRecord[String]]
    ds2.printSchema()
    /*
    root
     |-- key: binary (nullable = true)
     |-- value: binary (nullable = true)
     |-- topic: string (nullable = true)
     |-- partition: integer (nullable = true)
     |-- offset: long (nullable = true)
     |-- timestamp: timestamp (nullable = true)
     |-- timestampType: integer (nullable = true)
     |-- body: string (nullable = true)
    */

    ds2.select(get_json_object(($"body").cast("string"), "$.ip").alias("ip"),
      get_json_object(($"body").cast("string"), "$.url").alias("url")
    ).printSchema()

    /*
    root
     |-- ip: string (nullable = true)
     |-- url: string (nullable = true)
    */

    val streamingSelectDF = dsCorrect.select(
      get_json_object(($"value").cast("string"), "$.ip").alias("ip"),
      get_json_object(($"value").cast("string"), "$.url").alias("url"),
      date_format(get_json_object(($"value").cast("string"), "$.time"), "dd.MM.yyyy").alias("day"))

      .groupBy($"ip",$"url")
      .count()
      .as[(String,String, String)]

    streamingSelectDF.printSchema()
    /*
    root
     |-- ip: string (nullable = true)
     |-- url: string (nullable = true)
     |-- count: long (nullable = false)
    */

    // Start running the query that prints the running counts to the console
    val query = streamingSelectDF.writeStream
      .format("console")
      .outputMode("complete")
      .option("checkpointLocation", checkpointLocation)
      .start()


    query.awaitTermination()

  }
}
