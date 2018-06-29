package com.zt.spark

import java.util.UUID

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{DataTypes, StructType}
import org.apache.spark.sql._;

case class Coltest(name: String, age: Int) extends Serializable //定义字段名和类型
case class People(id: Int, name: String, age: Int, height: Int) extends Serializable

object RddDSDFTrans extends App {
  val checkpointLocation =
    if (args.length > 3) args(3) else "/tmp/temporary-" + UUID.randomUUID.toString

  val spark = SparkSession
    .builder()
    .master("local")
    .appName("wordcount Window")
    .getOrCreate()


  val rdd: RDD[(String, Int)] = spark.sparkContext.parallelize(Seq(("a", 1), ("b", 1), ("a", 1)))

  import spark.implicits._

  //RDD 转 DF
  val testDF: DataFrame = rdd.map { line =>
    (line._1, line._2)
  }.toDF("name", "age")

  testDF.select("name", "age").show()

  //RDD 转 DS
  val testDS: Dataset[Coltest] = rdd.map { line =>
    Coltest(line._1, line._2)
  }.toDS

  testDS.show()

  //DS 转 DF
  val testDF2: DataFrame = testDS.toDF("newName", "newAge")
  testDF2.printSchema()
  //DF 转 DS
  val testDS2: Dataset[Coltest] = testDF2.map { row => Coltest(row.getAs[String]("newName"), row.getAs[Int]("newAge")) }

  testDS2.printSchema()
  //DataFrame/Dataset 转 RDD：

  val rdd1: RDD[Row] = testDF.rdd
  val rdd2: RDD[Coltest] = testDS.rdd


  val linesRDD = spark.sparkContext.parallelize(Seq("1,zhang,28,174", "2,li,20,178", "3,wang,27,190"))

  val rowRDD = linesRDD.map(line => {
    val splits = line.split(",")
    Row(splits(0).trim.toInt, splits(1).trim, splits(2).trim.toInt, splits(3).trim.toInt)
  })

  val structType = StructType(Array(
    DataTypes.createStructField("id", DataTypes.IntegerType, true),
    DataTypes.createStructField("name", DataTypes.StringType, true),
    DataTypes.createStructField("age", DataTypes.IntegerType, true),
    DataTypes.createStructField("height", DataTypes.IntegerType, true)
  ))

  val peopleDF = spark.createDataFrame(rowRDD, structType)
  peopleDF.printSchema()
  peopleDF.createOrReplaceTempView("people")
  spark.sql("select avg(age) from people").show()



  val schema = Encoders.product[People].schema
  val peopleDF2 = spark.createDataFrame(rowRDD, schema)
  peopleDF2.printSchema()
  peopleDF2.createOrReplaceTempView("people2")
  spark.sql("select avg(age) from people2").show()



}
