package com.zt.spark.mongo

import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config.ReadConfig
import org.apache.spark.internal.Logging
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

object MongoTemplate extends Logging with Serializable {

  def read(collection: String, uri: String, database: String, spark: SparkSession): RDD[String] = {
    /**
      * 若使用uri方式指定database，存在admin下创建的账密无法在其他的database下使用的问题
      */
    val readConfig = ReadConfig(Map(
      "collection" -> collection,
      "uri" -> uri,
      "database" -> database
    ))
    MongoSpark.load(spark.sparkContext, readConfig).map(_.toJson())
  }

}

