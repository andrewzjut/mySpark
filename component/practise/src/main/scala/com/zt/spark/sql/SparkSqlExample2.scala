package com.zt.spark.sql

import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.streaming.DataStreamReader
import org.apache.spark.sql.{DataFrameWriter, Dataset, Encoders, SparkSession}

case class Person(id: Long, name: String)

object SparkSqlExample2 extends App with Logging {
  val spark = SparkSession
    .builder().master("local")
    .appName("Spark SQL basic example")
    .config("spark.sql.warehouse.dir", "/tmp/spark-warehouse")
    .getOrCreate()

  import org.apache.spark.sql.functions._

  val myUpper = udf((s: String) => s.toUpperCase)

  spark.udf.register("myUpper", myUpper)

  import spark.implicits._

  val strs = ('a' to 'c').map(_.toString).toDS

  strs.createOrReplaceTempView("strs")

  spark.sql("SELECT *, myUpper(value) UPPER FROM strs").show
  strs.withColumn("UPPER", myUpper($"value")).show


  val personEncoder = Encoders.product[Person]
  personEncoder.schema
  personEncoder.clsTag
  val personExprEncoder = personEncoder.asInstanceOf[ExpressionEncoder[Person]]
  val jacek = Person(0, "Jacek")
  val row = personExprEncoder.toRow(jacek)

  import org.apache.spark.sql.catalyst.dsl.expressions._

  val attrs = Seq(DslSymbol('id).long, DslSymbol('name).string)
  val jacekReborn = personExprEncoder.resolveAndBind(attrs).fromRow(row)

  log.info("equals:{}", jacek == jacekReborn)
  log.info("{}", Encoders.kryo[Person])

  //  val reader = spark.read
  //  val people = reader.csv("people.csv")
  //  val cities = reader.format("json").load("cities.json")
  //  spark.read.textFile("README.md")
  //  val countries = reader.format("customFormat").load("countries.cf")
  //  val stream: DataStreamReader = spark.readStream
  //  val ints: Dataset[Int] = (0 to 5).toDS
  //  val writer: DataFrameWriter[Int] = ints.write
  //  writer.format("json").save("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/writer")

  val dataset = spark.range(start = 0, end = 4, step = 1, numPartitions = 2)
  val mycount = MyAggFunction

  dataset.show()

  val q = dataset.
    withColumn("group", 'id % 2).
    groupBy("group").
    agg(mycount.distinct('id) as "count")
  q.show()


}
