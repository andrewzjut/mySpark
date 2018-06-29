package com.zt.scala.`implicit`

import java.io.File

import scala.io.Source

object Implicit {
  def main(args: Array[String]): Unit = {
    implicit def double2Int(x:Double)=x.toInt
    val x:Int = 3.5
    println(x)
    implicit def file2RichFile(file: File)=new RichFile(file)
    val f = new File("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/log4j.properties").read
    println(f)
  }
}

class RichFile(val file:File){
  def read = Source.fromFile(file).getLines().mkString
}