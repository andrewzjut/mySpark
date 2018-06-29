package com.zt.spark.streaming

import com.zt.scala.common.Person
import org.apache.spark.sql.functions.get_json_object
import org.apache.spark.sql.{Encoders, SparkSession}

object StructuredNetwork2 {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("StructuredNetworkWordCount")
      .getOrCreate()

    val schema = Encoders.product[Person].schema

    import spark.implicits._
    val socketDF = spark.readStream
      .format("socket")
      .option("host","localhost")
      .option("port","9999")
      .load()

    socketDF.printSchema

    val ds = socketDF.select(
      get_json_object(($"value").cast("string"), "$.name").alias("name"),
      get_json_object(($"value").cast("string"), "$.age").alias("age")
    )

    ds.printSchema()


    val query = ds.writeStream.format("console").start()

    query.awaitTermination()
  }
}
