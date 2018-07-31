package com.zt.spark.sql

import org.apache.spark.sql.{SparkSession}
import org.apache.spark.sql.expressions.Window

case class Record(Hour: Integer, Category: String, TotalValue: Double)

object FirstRowOfEachGroup extends App {

  val spark = SparkSession
    .builder().master("local")
    .appName("根据分组取出 某个值 最大的行 ")
    .config("spark.sql.warehouse.dir", "/tmp/spark-warehouse")
    .getOrCreate()

  import spark.implicits._

  val df = spark.sparkContext.parallelize(Seq(
    (0, "cat26", 30.9),
    (0, "cat13", 22.1),
    (0, "cat95", 19.6),
    (0, "cat105", 1.3),
    (1, "cat67", 28.5),
    (1, "cat4", 26.8),
    (1, "cat13", 12.6),
    (1, "cat23", 5.3),
    (2, "cat56", 39.6),
    (2, "cat40", 29.7),
    (2, "cat187", 27.9),
    (2, "cat68", 9.8),
    (3, "cat8", 35.6)
  )).toDF("Hour", "Category", "TotalValue")

  import org.apache.spark.sql.functions._

  //1
  val w = Window.partitionBy($"hour").orderBy($"TotalValue".desc)

  val dfTop = df.withColumn("rn", row_number.over(w)).where($"rn" === 1).drop("rn")

  dfTop.show
  //2
  val dfMax = df.groupBy($"hour".as("max_hour")).agg(max($"TotalValue").as("max_value"))
  val dfTopByJoin = df.join(broadcast(dfMax),
    ($"hour" === $"max_hour") && ($"TotalValue" === $"max_value"))
    .drop("max_hour")
    .drop("max_value")

  dfTopByJoin.show


  dfTopByJoin
    .groupBy($"hour")
    .agg(
      first("category").alias("category"),
      first("TotalValue").alias("TotalValue"))
    .show()


  //3
  df.select($"Hour", struct($"TotalValue", $"Category").alias("vs"))
    .groupBy($"hour")
    .agg(max("vs").alias("vs"))
    .select($"Hour", $"vs.Category", $"vs.TotalValue").show()

  //4 spark 2.0
  df.as[Record]
    .groupByKey(_.Hour)
    .reduceGroups((x, y) => if (x.TotalValue > y.TotalValue) x else y)
    .map(kv => kv._2).show()

  // spark 1.6
  //  df.as[Record]
  //    .groupBy($"hour")
  //    .reduce((x, y) => if (x.TotalValue > y.TotalValue) x else y)
  //    .show

  //  val w2 = Window.partitionBy($"col1", $"col2", $"col3").orderBy($"timestamp".desc)
  //  val refined_df = df.withColumn("rn", row_number.over(w2)).where($"rn" === 1).drop("rn")

  df.createTempView("table")

  //使用window
  spark.sql(
    """
      |select
      |Hour, Category, TotalValue
      |from
      |     (select *, row_number() OVER (PARTITION BY Hour ORDER BY TotalValue DESC) as rn  FROM table) tmp
      |where rn = 1
      |""".stripMargin
  ).show(false)

  //使用join
  spark.sql("select Hour, first(Category) as Category, first(TotalValue) as TotalValue from " +
    "(select Hour, Category, TotalValue from table tmp1 " +
    "join " +
    "(select Hour as max_hour, max(TotalValue) as max_value from table group by Hour) tmp2 " +
    "on " +
    "tmp1.Hour = tmp2.max_hour and tmp1.TotalValue = tmp2.max_value) tmp3 " +
    "group by tmp3.Hour")
    .show(false)

  spark.sql(
    """
      |select
      |Hour, vs.Category, vs.TotalValue
      |from
      | (select Hour, max(struct(TotalValue, Category)) as vs from table group by Hour)""".stripMargin).show(false)

  import org.apache.spark.sql.Row
  import org.apache.spark.sql.catalyst.encoders.RowEncoder

  implicit val dfEnc = RowEncoder(df.schema)

  df.groupByKey { (r) => r.getInt(0) }
    .mapGroups { (_: Int, rows: Iterator[Row]) => rows.maxBy { (r) => r.getDouble(2) } }.show()


  val keys = List("Hour", "Category");
  val selectFirstValueOfNoneGroupedColumns =
    df.columns
      .filterNot(keys.toSet)
      .map(_ -> "first").toMap
  val grouped =
    df.groupBy(keys.head, keys.tail: _*)
      .agg(selectFirstValueOfNoneGroupedColumns)


  df.
    groupBy("Hour")
    .agg(
      first("Hour").as("_1"),
      first("Category").as("Category"),
      first("TotalValue").as("TotalValue")
    ).drop("Hour")
    .withColumnRenamed("_1", "Hour")
    .show
}
