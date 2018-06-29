package com.zt.spark.rdd

import java.util.Random

import org.apache.spark.sql.SparkSession
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}

object GroupByTest {
  def main(args: Array[String]) {
    var numMappers = 100
    var numKVPairs = 10000
    var valSize = 1000
    var numReducers = 36

    val spark = SparkSession
      .builder().master("local[2]")
      .appName("GroupByTest")
      .getOrCreate()

    //    val data1 = Array[(Int, Char)](
    //      (1, 'a'), (2, 'b'),
    //      (3, 'c'), (4, 'd'),
    //      (5, 'e'), (3, 'f'),
    //      (2, 'g'), (1, 'h'))
    //    val rangePairs1 = spark.sparkContext.parallelize(data1, 3)
    //
    //    val hashPairs1 = rangePairs1.partitionBy(new HashPartitioner(3))
    //
    //    val data2 = Array[(Int, String)]((1, "A"), (2, "B"),
    //      (3, "C"), (4, "D"))
    //
    //    val pairs2 = spark.sparkContext.parallelize(data2, 2)
    //    val rangePairs2 = pairs2.map(x => (x._1, x._2.charAt(0)))
    //
    //    val data3 = Array[(Int, Char)]((1, 'X'), (2, 'Y'))
    //    val rangePairs3 = spark.sparkContext.parallelize(data3, 2)
    //
    //    val rangePairs = rangePairs2.union(rangePairs3)
    //
    //    val result = hashPairs1.join(rangePairs)
    //
    //    result.foreach {
    //      x => println("[result " + x._1 + "] " + x._2._1 + " " + x._2._2)
    //    }
    //
    //    val a = spark.sparkContext.parallelize(Seq(1, 2, 3), 3)
    //    val b = spark.sparkContext.parallelize(Seq('a', 'b', 'c'), 3)
    //    a.cartesian(b).collect().foreach(x => println(s"(${x._1} , ${x._2})"))
    //
    //    val cartesain = for (a <- Seq(1, 2, 3); b <- Seq('a', 'b', 'c')) yield (a, b)
    //
    //    println(cartesain)

    val sc = spark.sparkContext

    val av = sc.parallelize(Seq("Mommy", "Baby", "Daddy"), 3)
      .map {
        case "Mommy" => ("Mommy", "Welcome to our little family,i love you,  ")
        case "Daddy" => ("Daddy", "Welcome to world ,my little baby, ")
        case "Baby" => ("Baby", "Hello world, ")
      }.aggregate("We are family! ")((_, y) => y._1 + " says: " + y._2, _ + _)

    println(av)

    sc.parallelize(Seq("Mommy", "Baby", "Daddy"), 3).map(_.toLowerCase()).count()

    Thread.sleep(22222222L)
    spark.stop()
  }
}