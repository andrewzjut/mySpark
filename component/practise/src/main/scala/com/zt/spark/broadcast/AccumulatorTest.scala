package com.zt.spark.broadcast

import org.apache.spark.sql.SparkSession

object AccumulatorTest {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[2]")
      .appName("AccumulatorTest")
      .getOrCreate()
    val count = spark.sparkContext.longAccumulator("count")
    val data = spark.sparkContext.parallelize(Array(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

    data.foreach { i => count.add(i) }

    println(count.value)
    println(count.sum)
    println(count.count)

    data.count()
    spark.stop()

  }
}
