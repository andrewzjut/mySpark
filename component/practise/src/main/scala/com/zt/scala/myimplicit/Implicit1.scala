package com.zt.scala.myimplicit

import java.io.File

import scala.io.Source

object Implicit {
  def main(args: Array[String]): Unit = {
    implicit def double2Int(x:Double)=x.toInt
    val x:Int = 3.5
    println(x)
    implicit def file2RichFile(file: File)=new RichFile(file)
    val file = new File("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/log4j.properties")
    println(file.read)
  }
}

class RichFile(val file:File){
  def read = Source.fromFile(file).getLines().mkString
}