package com.zt.spark.dataset

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * Created by hzzt on 2017/2/9.
  */
object WordCount {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .master("local[3]")
      .appName("wordcount")
      .config("spark.sql.warehouse.dir", "file:///Users/zhangtong/IdeaProjects/mySpark/src/main/resources/spark-warehouse")
      .config("num-executors", "2")
      .getOrCreate()

    //    import sparkSession.implicits._
    //    val data = sparkSession.read.text("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/log4j.properties").as[String]
    //
    //    val word = data.flatMap(value => value.split("\\s+"))
    //    val groupedWords = word.groupByKey(_.toLowerCase)
    //    val counts = groupedWords.count()
    //    counts.show(10)


    val lines = spark.sparkContext
      .textFile("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/log4j.properties")
    val words = lines.flatMap(_.split(" ")).filter(word => word != " ")
    val pairs = words.map(word => (word, 1))
    val wordCounts = pairs.reduceByKey(_ + _)
    wordCounts.collect.foreach(println)

    import spark.implicits._

    words.filter(word => !word.trim.equals("")).toDF("word").groupBy("word").count().show()

    spark.stop()
  }
}
