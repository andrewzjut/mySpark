package com.zt.spark.file

import com.zt.scala.common.{StreamRecord, Visit}
import org.apache.spark.sql.{Encoders, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StringType

object FileTest1 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[2]").appName("FileTest1").getOrCreate()
    val data = spark.read.text("/Users/zhangtong/Documents/b.txt")
    data.printSchema()
    val set: Set[String] = Set("000000056286511",
      "000000463020355",
      "000000787750758",
      "000000494658642",
      "000001065677496")
    import spark.implicits._
    val schema = Encoders.product[DopVisitInfoDTO].schema

    data
      .withColumn("PageSessionID", get_json_object(($"value").cast("string"), "$.formatted.PageSessionID"))
      .filter {
      row =>
        val PageSessionId = row.getAs[String]("PageSessionID")
        if (null != PageSessionId) {
          val psId = "%015d" format Math.abs(PageSessionId.hashCode)
          if (set.contains(psId)) true else false
        } else {
          false
        }

    }.drop("PageSessionID").rdd.repartition(1).saveAsTextFile("/Users/zhangtong/Documents/b")

  }
}
