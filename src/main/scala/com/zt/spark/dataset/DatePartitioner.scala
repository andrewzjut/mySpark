package com.zt.spark.dataset

import org.apache.spark.Partitioner

class DatePartitioner(numParts: Int) extends Partitioner {
  //覆盖分区数
  override def numPartitions: Int = numParts

  //覆盖分区号获取函数
  override def getPartition(key: Any): Int = {
    Math.abs(key.toString.hashCode % numPartitions)
  }
}
