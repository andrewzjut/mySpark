package com.zt.spark.rdd

import java.util.concurrent.atomic.AtomicLong

import com.typesafe.scalalogging.LazyLogging
import org.apache.spark.sql.SparkSession

object HugeDataProcess extends App with LazyLogging {


  def pt_convert(idx: Int, ds: Iterator[String], seq: Int): Iterator[String] = {
    if (seq == idx) ds else Iterator()
  }

  val sc = SparkSession.builder
    .master(args(0))
    .config("driver-memory", "1024M")
    .config("executor-memory", "1024M")
    .appName("recommendation").getOrCreate()

  val start = System.currentTimeMillis()
  //加载HDFS数据
  val rdd = sc.sparkContext.textFile(args(1))

  //在驱动程序获取结果集
  val counts = new AtomicLong(0)
  //重分区并合理优化分区个数
  val new_rdd = rdd.coalesce(10)
  //得到所有的分区信息
  val parts = new_rdd.partitions
  //  循环处理每个分区的数据，避免导致OOM
  for (p <- parts) {
    //获取分区号
    val idx = p.index
    //第二个参数为true，避免重新shuffle，保留原始分区数据
    val parRdd = new_rdd.mapPartitionsWithIndex[String](pt_convert(_, _, idx), true)
    //读取结果数据
    val data = parRdd.collect()
    //循环处理数据
    for (_ <- data) {
      counts.incrementAndGet()
    }
  }

  val end = System.currentTimeMillis()
  println((end - start) / 1000)
  println(counts)
}
