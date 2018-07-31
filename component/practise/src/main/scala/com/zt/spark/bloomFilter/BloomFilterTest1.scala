package com.zt.spark.bloomFilter

import java.util.concurrent.atomic.LongAccumulator

import breeze.util.BloomFilter
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.types.{DataTypes, StructType}
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.util.LongAccumulator

object BloomFilterTest1 extends App {
  val spark = SparkSession.builder()
    .master("local[2]")
    .appName("BloomFilterTest1")
    .getOrCreate()

  //  for (i <- 0 to 100) {
  //
  //    val userId = new Random().nextInt(100)
  //
  //    val hour = new Random().nextInt(23)
  //    val minute = new Random().nextInt(59)
  //    val second = new Random().nextInt(59)
  //
  //    val localDateTime: LocalDateTime = LocalDateTime.of(localDate.getYear, localDate.getMonth, localDate.getDayOfMonth - 1, hour, minute, second)
  //    buffer.+=(("a" + userId, localDateTime.format(formatter)))
  //
  //  }
  //  for (i <- 0 to 100) {
  //
  //    val userId = new Random().nextInt(100)
  //
  //    val hour = new Random().nextInt(23)
  //    val minute = new Random().nextInt(59)
  //    val second = new Random().nextInt(59)
  //
  //    val localDateTime: LocalDateTime = LocalDateTime.of(localDate.getYear, localDate.getMonth, localDate.getDayOfMonth - 1, hour, minute, second)
  //    buffer.+=(("a" + userId, localDateTime.format(formatter)))
  //
  //  }
  //
  //  for (i <- 0 to 100) {
  //
  //    val userId = new Random().nextInt(100)
  //
  //    val hour = new Random().nextInt(23)
  //    val minute = new Random().nextInt(59)
  //    val second = new Random().nextInt(59)
  //
  //    val localDateTime: LocalDateTime = LocalDateTime.of(localDate.getYear, localDate.getMonth, localDate.getDayOfMonth - 2, hour, minute, second)
  //    buffer.+=(("a" + userId, localDateTime.format(formatter)))
  //
  //  }
  //
  //  for (i <- 0 to 100) {
  //
  //    val userId = new Random().nextInt(100)
  //
  //    val hour = new Random().nextInt(23)
  //    val minute = new Random().nextInt(59)
  //    val second = new Random().nextInt(59)
  //
  //    val localDateTime: LocalDateTime = LocalDateTime.of(localDate.getYear, localDate.getMonth, localDate.getDayOfMonth - 2, hour, minute, second)
  //    buffer.+=(("a" + userId, localDateTime.format(formatter)))
  //
  //  }
  //  buffer.foreach(x => println(x))


  val file = spark.sparkContext.textFile("component/practise/src/main/scala/com/zt/spark/bloomFilter/visit.txt")

  val data = file.map {
    x =>
      val xx = x.replaceAll("\\(|\\)", "")
      val kv = xx.split(",")
      (kv(0), kv(1))
  }.map(kv => Row(kv._1, kv._2))


  val structType = StructType(Array(
    DataTypes.createStructField("user_id", DataTypes.StringType, true),
    DataTypes.createStructField("his", DataTypes.StringType, true)
  ))

  val access_log = spark.createDataFrame(data, structType)

  access_log.printSchema()
  access_log.show()

  access_log.createOrReplaceTempView("access_log")

  val yesterdayDF = spark.sql(
    """
      |select
      |    al.user_id
      |from access_log al
      |where date_format(al.his,'yyyy-MM-dd') = '2018-07-29'
      |and al.user_id is not null
    """.stripMargin)

  yesterdayDF.show()

  //  val bf = yesterdayDF.stat.bloomFilter("user_id", 200000, 0.01) // 布隆过滤器
  //  import org.apache.spark.util.sketch.BloomFilter
  //
  //  val bfBc: Broadcast[BloomFilter] = spark.sparkContext.broadcast(bf) // 广播变量
  //
  //  val count = spark.sql(
  //    """
  //      |select
  //      |    al.user_id
  //      |from access_log al
  //      |where al.his = date_format(date_sub(current_date, 2), 'yyyy-MM-dd')
  //      |and al.user_id is not null
  //    """.stripMargin).filter((row) => {
  //    bfBc.value.mightContainString(row.getString(0))
  //  }).distinct().count()

  //  println(count)

  val numBuckets: Int = 202
  val numHashFunctions: Int = 3
  val bloomFilter = new BloomFilter[String](numBuckets, numHashFunctions)
  val bf = spark.sparkContext.broadcast(bloomFilter)

  val uv = spark.sparkContext.longAccumulator("uv")
  val pv = spark.sparkContext.longAccumulator("pv")

  yesterdayDF.foreach(row => {
    val userId = row.getAs[String]("user_id")
    if (!bf.value.contains(userId)) {
      bf.value.+=(userId)
      uv.add(1)
    }
    pv.add(1)
  })


  val pvuv = spark.sql(
    """
      |select
      |    count(*) pv ,count(distinct(al.user_id)) uv
      |from access_log al
      |where date_format(al.his,'yyyy-MM-dd') = '2018-07-29'
      |and al.user_id is not null
    """.stripMargin)

  println(s"pv=${pv.value}  uv=${uv.value}")
  pvuv.show()
}
