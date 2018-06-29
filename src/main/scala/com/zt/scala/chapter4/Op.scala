package com.zt.scala.chapter4

object Op extends Enumeration {
  type Op = Value
  val EQ = Value(0, "=")
  val NE = Value(1, "!=")
  val LTGT = Value(2, "<>")
  val LT = Value(3, "<")
  val LE = Value(4, "<=")
  val GT = Value(5, ">")
  val GE = Value(6, ">=")
}
