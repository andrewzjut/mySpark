package com.zt.spark.mongo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.zt.scala.utils.JsonHelper
import org.apache.spark.sql.{Row, SparkSession}

import scala.util.parsing.json.JSONObject

object MongoTest1 extends App {


  val spark = SparkSession.builder()
    .master("local")
    .appName("MongoSparkConnectorIntro")
    .config("spark.mongodb.input.uri", "mongodb://10.200.130.122/zt.yes_users")
    .config("spark.mongodb.output.uri", "mongodb://10.200.130.122/zt.yes_users")

    .getOrCreate()

  val readConfig = ReadConfig(Map("collection" -> "yes_users", "readPreference.name" -> "secondaryPreferred"), Some(ReadConfig(spark)))
  val customDf = MongoSpark.load(spark, readConfig)
  customDf.toJSON
  import scala.collection.JavaConverters._

  println(customDf.count)
  val mapper = new ObjectMapper with ScalaObjectMapper

  customDf.foreach { row =>
    println(row)
    println(mapper.writeValueAsString(row.getValuesMap(row.schema.fieldNames).asJava))
  }
  import com.fasterxml.jackson.module.scala.DefaultScalaModule

//  mapper.registerModule(DefaultScalaModule)
  val map = Map(1->2)
  println(mapper.writeValueAsString(map.asJava))

  println(mapper.writeValueAsString(customDf.first().getValuesMap(customDf.first().schema.fieldNames).asJava))



  import org.bson.Document

  val documents = spark.sparkContext.parallelize((1 to 2).map(i => Document.parse(s"{test: $i}")))

  MongoSpark.save(documents) // Uses the SparkConf for configuration

}
