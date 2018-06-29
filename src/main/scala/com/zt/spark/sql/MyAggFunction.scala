package com.zt.spark.sql

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, LongType, StructType}

object MyAggFunction extends UserDefinedAggregateFunction {
  override def inputSchema: StructType =
    new StructType().add("id", LongType, nullable = true)


  override def bufferSchema: StructType =
    new StructType().add("count", LongType, nullable = true)

  override def dataType: DataType = LongType

  override def deterministic: Boolean = true

  override def initialize(buffer: MutableAggregationBuffer): Unit = {
    println(s">>> initialize (buffer: $buffer)")
    // NOTE: Scala's update used under the covers
    buffer(0) = 0L
  }

  override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
    println(s">>> update (buffer: $buffer -> input: $input)")
    buffer(0) = buffer.getLong(0) + 1
  }

  override def merge(buffer: MutableAggregationBuffer, row: Row): Unit = {
    println(s">>> merge (buffer: $buffer -> row: $row)")
    buffer(0) = buffer.getLong(0) + row.getLong(0)
  }

  override def evaluate(buffer: Row): Any = {
    println(s">>> evaluate (buffer: $buffer)")
    buffer.getLong(0)
  }
}
