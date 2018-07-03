package com.zt.spark.rdd

import org.apache.spark.{SparkConf, SparkContext}

class Sort {

}

object Sort {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("recommendation")
    val sc = new SparkContext(conf)

    val data = sc.textFile("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/a.txt", 1)

    val sortdata = data
      .map(str => str.trim)
      .distinct()
      .map(str => (str, 1))
      .sortByKey()

    sortdata.keys.saveAsTextFile("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/out")

  }
}
