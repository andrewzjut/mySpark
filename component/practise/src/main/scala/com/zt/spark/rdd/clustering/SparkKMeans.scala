package com.zt.spark.rdd.clustering

import breeze.linalg.{DenseVector, Vector, squaredDistance}
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by hzzt on 2016/11/22.
  */
object SparkKMeans {

  def parseVector(line: String): Vector[Double] = {
    DenseVector(line.split("\\s+").map(_.toDouble))
  }

  def closestPoint(p: Vector[Double], centers: Array[Vector[Double]]): Int = {
    var bestIndex = 0
    var closest = Double.PositiveInfinity

    for (i <- 0 until centers.length) {
      val tempDist = squaredDistance(p, centers(i))
      if (tempDist < closest) {
        closest = tempDist
        bestIndex = i
      }
    }

    bestIndex
  }

  def main(args: Array[String]) {


    val conf = new SparkConf().setAppName("SparkKMeans").setMaster("local")
    val sc = new SparkContext(conf)
//    sc.addJar("D:\\myPlace\\Spark2.0Learnning\\out\\artifacts\\Spark2_0Learnning_jar\\Spark2.0Learnning.jar")

    val lines = sc.textFile("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/kmeans_data.txt")

    val data = lines.map(parseVector _).cache()
    data.collect().foreach(println)
    val K = 3
    val convergeDist = 0.01

    val kPoints = data.takeSample(withReplacement = false, K, 42)
    var tempDist = 1.0

    while (tempDist > convergeDist) {

      val closest = data.map(p => (closestPoint(p, kPoints), (p, 1)))

      val pointStats = closest.reduceByKey { case ((p1, c1), (p2, c2)) => (p1 + p2, c1 + c2) }

      val newPoints = pointStats.map { pair =>
        (pair._1, pair._2._1 * (1.0 / pair._2._2))
      }.collectAsMap()

      tempDist = 0.0
      for (i <- 0 until K) {
        tempDist += squaredDistance(kPoints(i), newPoints(i))
      }

      for (newP <- newPoints) {
        kPoints(newP._1) = newP._2
      }
      println("Finished iteration (delta = " + tempDist + ")")
    }

    println("Final centers:")
    kPoints.foreach(println)
//    sc.stop()
  }
}