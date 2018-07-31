//package com.zt.spark.bloomFilter
//
//import java.util.UUID
//
//import bloomfilter.CanGenerateHashFrom
//import org.apache.spark.sql.SparkSession
//import org.apache.spark.sql.types.{DataTypes, StructType}
//
//import scala.collection.mutable.ArrayBuffer
//
//case class Visit(val userId: String, val his: String) {
//  override def hashCode(): Int = (userId + his).hashCode
//}
//
//object BloomFilterTest2 {
//
//  implicit case object CanGenerateHashFromVisit extends CanGenerateHashFrom[Visit] {
//    override def generateHash(from: Visit): Long = (from.userId + from.his).hashCode
//  }
//
//  def main(args: Array[String]): Unit = {
//
//    val spark = SparkSession.builder()
//      .master("local[2]")
//      .appName("BloomFilterTest1")
//      .getOrCreate()
//
//    val buffer = new ArrayBuffer[(String, String)]
//    for (i <- 0 until 1000000) {
//      val userId = s"a$i"
//      buffer.+=((userId, "2018-07-28"))
//    }
//    val data = spark.sparkContext.parallelize(buffer).map(x => Visit(x._1, x._2))
//
//    import spark.implicits._
//
//    val access_log = data.toDF
//    access_log.createOrReplaceTempView("access_log")
//
//
//    import bloomfilter.mutable.BloomFilter
//
//    val expectedElements = 1000000
//    val falsePositiveRate = 0.01
//    val bf = BloomFilter[Visit](expectedElements, falsePositiveRate)
//
//
//    val bloomFilter = spark.sparkContext.broadcast(bf)
//
//    val uv = spark.sparkContext.longAccumulator("uv")
//    val pv = spark.sparkContext.longAccumulator("pv")
//
//    access_log.rdd.foreachPartition(
//      rdd =>
//        rdd.foreach {
//          row =>
//            val userId = row.getAs[String]("userId")
//            val his = row.getAs[String]("his")
//            val visit = Visit(userId, his)
//            if (!bloomFilter.value.mightContain(visit)) {
//              bloomFilter.value.add(visit)
//              uv.add(1)
//            }
//            pv.add(1)
//
//        })
//
//
//    println(s"pv=${pv.count}  uv=${uv.count}")
//    //  val pvuv = spark.sql(
//    //    """
//    //      |select
//    //      |    count(*) pv ,count(distinct(al.userId)) uv
//    //      |from access_log al
//    //      |where date_format(al.his,'yyyy-MM-dd') = '2018-07-28'
//    //      |and al.userId is not null
//    //    """.stripMargin)
//    //
//    //  pvuv.show()
//
//    bloomFilter.value.dispose()
//  }
//
//
//}
