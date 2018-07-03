package com.zt.spark.rddoperation

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.util.{CollectionAccumulator, DoubleAccumulator, LongAccumulator}

object RddOperation extends App {

  val spark = SparkSession
    .builder()
    .master("local[2]")
    .appName("RddOperation")
    .getOrCreate()

  val data = Array(1, 2, 3, 4, 5)
  val distData = spark.sparkContext.parallelize(data)

  distData.map(x => x * 2).collect().foreach(x => print(x + " "))
  println()
  distData.map(MyFunctions.addOne).collect().foreach(x => print(x + " "))
  println()

  var counter1: Int = 0
  var rdd1 = spark.sparkContext.parallelize(data)
  rdd1.collect().foreach(x => counter1 += 1)
  println("Counter1:" + counter1)

  //一般情况下, Spark 的 map 或者 reduce 操作(task)的方法是运行在远程的集群节点上的，
  // 且会在每一个操作上复制一份变量。因为节点之间的变量不会共享，所以在远程机器上的变量的
  // 更新不会传播到驱动器程序上。通用的解决方法，就是使用可以被全部的 task 读写的共享变量，
  // 但他会拖慢运行效率。然而， Spark 还是为两种普遍的使用模式提供了两种共享变量的受限类型：广播变量与增量器。

  //使用原生的数字类型的增量器
  var counter = spark.sparkContext.longAccumulator("My Accumulator")
  var rdd = spark.sparkContext.parallelize(data)
  rdd.foreach(x => counter.add(1))
  println("Counter:" + counter.value)

  //自定义类型累加器
  val myAcc = new MyAccumulator
  spark.sparkContext.register(myAcc, "myAcc")
  val nums = Array("1", "2", "3", "4", "5", "6", "7", "8")
  val numsRdd = spark.sparkContext.parallelize(nums)
  numsRdd.foreach(n => myAcc.add(n))
  println(myAcc.value)

  //使用自定义累加器求和
  val longAccumulator = MyFunctions.longAccumulator(spark, "longAccumulator")
  distData.foreach(n => longAccumulator.add(n))
  println(longAccumulator.value)

  //使用自定义集合累加器
  val collectionAccumulator = MyFunctions.collectionAccumulator[Int](spark, "collectionAccumulator")
  distData.foreach(n => collectionAccumulator.add(n))
  println(collectionAccumulator.value)


  val broadcastVar = spark.sparkContext.broadcast(Array(1, 2, 3))



}

object MyFunctions {
  val field = "Hello"

  def toUpper(s: String): String = s.toUpperCase

  def addOne(s: Int): Int = s + 1

  def doStuff(rdd: RDD[String]): RDD[String] = {
    rdd.map(x => field + x)
  }

  //创建并注册一个long accumulator, 从“0”开始，用“add”累加
  def longAccumulator(sparkSession: SparkSession, name: String): LongAccumulator = {
    val acc = new LongAccumulator
    sparkSession.sparkContext.register(acc, name)
    acc
  }

  //创建并注册一个double accumulator, 从“0”开始，用“add”累加
  def doubleAccumulator(sparkSession: SparkSession, name: String): DoubleAccumulator = {
    val acc = new DoubleAccumulator
    sparkSession.sparkContext.register(acc, name)
    acc
  }

  //创建并注册一个CollectionAccumulator, 从“empty list”开始，并加入集合
  def collectionAccumulator[T](sparkSession: SparkSession, name: String): CollectionAccumulator[T] = {
    val acc = new CollectionAccumulator[T]
    sparkSession.sparkContext.register(acc, name)
    acc
  }
}

