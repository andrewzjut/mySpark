package com.zt.spark.streaming

import java.util.UUID

import com.zt.scala.common.Person
import com.zt.spark.streaming.util.KafkaSink
import org.apache.spark.sql.{Dataset, Encoders, SparkSession}

object CSVStream {
  def main(args: Array[String]): Unit = {
    val checkpointLocation =
      if (args.length > 3) args(3) else "/tmp/temporary-" + UUID.randomUUID.toString

    val spark = SparkSession
      .builder
      .master("local")
      .appName("CSV")
      .getOrCreate()

    val userSchema = Encoders.product[Person].schema

    import spark.implicits._
    val csvDF = spark
      .readStream
      .option("sep", ",")
      .schema(userSchema) // Specify schema of the csv files
      .csv("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/csv") // Equivalent to format("csv").load("/path/to/directory")

    csvDF.printSchema()

    val age = csvDF.select($"name", $"age" + 1)
    val older = csvDF.select("name", "age").where("age > 20")
    val csvDS = csvDF.as[Person]
    csvDS.filter(_.age > 20).map(_.name)
    csvDF.groupBy("name").count()

    import org.apache.spark.sql.expressions.scalalang.typed
    val avg = csvDS.groupByKey(_.name).agg(typed.avg(_.age))

    val query = csvDF.writeStream
      .format("console")
      //.outputMode("complete")
      .option("checkpointLocation", checkpointLocation)
      .start()

    csvDF.writeStream.foreach(new KafkaSink("kafkaStreamWriter", "10.200.131.154:9092,10.200.131.155:9092,10.200.131.156:9092"))
      .start()

    spark.streams.awaitAnyTermination()

  }
}
