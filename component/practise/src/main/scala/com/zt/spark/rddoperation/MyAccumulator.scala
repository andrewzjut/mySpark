package com.zt.spark.rddoperation

import org.apache.spark.util.AccumulatorV2

class MyAccumulator extends AccumulatorV2[String, String] {

  private var res = ""

  override def isZero: Boolean = res == ""

  override def copy(): AccumulatorV2[String, String] = {
    val newMyAcc = new MyAccumulator
    newMyAcc.res = this.res
    newMyAcc
  }

  override def reset(): Unit = res = ""

  override def add(v: String): Unit = res += v + "-"

  override def merge(other: AccumulatorV2[String, String]): Unit = other match {
    case o: MyAccumulator => res += o.res
    case _ => throw new UnsupportedOperationException(
      s"Cannot merge ${this.getClass.getName} with ${other.getClass.getName}")
  }

  override def value: String = res
}
