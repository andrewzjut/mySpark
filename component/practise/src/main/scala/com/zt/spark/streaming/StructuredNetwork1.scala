package com.zt.spark.streaming

import com.zt.scala.common.Person
import org.apache.spark.internal.Logging
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, _}

object StructuredNetwork1 extends Logging {
  def main(args: Array[String]) {
    val spark = SparkSession
      .builder()
      .master("local")
      .appName("StructuredNetworkWordCount")
      .getOrCreate()

    val userSchema = new StructType().add("name", "string").add("age", "integer")

    import spark.implicits._
    val socketDF = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", "9999")
      .load()

    socketDF.printSchema
    val people: Dataset[Person] = socketDF.as[String].map(_.split(",")).map(tp => Person(tp(0), tp(1).toInt))
    people.printSchema()
    val people2: DataFrame = socketDF.map(r => r.getString(0).split(",")).map(tp => (tp(0), tp(1).toInt)).toDF("name", "age")
    people2.printSchema()
    val count = people2.groupBy("name", "age").count()
    val query = count
      .writeStream
      .outputMode("complete")
      .format("console")
      .start()


    val socketDF2 = spark.readStream
      .format("socket")
      .option("host", "localhost")
      .option("port", "9998")
      .load()

    socketDF2.writeStream.foreach(new ForeachWriter[Row] {
      override def open(partitionId: Long, version: Long): Boolean = {
        log.error("初始化")
        true
      }

      override def process(value: Row): Unit = {
        log.error(s"Row: $value")
      }

      override def close(errorOrNull: Throwable): Unit = {
        log.error("关闭")
      }
    }).start()

    spark.streams.awaitAnyTermination()

  }
}
