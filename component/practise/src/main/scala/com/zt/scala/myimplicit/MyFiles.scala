package com.zt.scala.myimplicit

import java.io.{BufferedReader, File, FileReader}


class MyFiles(file: File) {
  def lines: Array[String] = {
    val fileReader: FileReader = new FileReader(file)
    val reader = new BufferedReader(fileReader)
    try {
      var lines = Array[String]()
      var line = reader.readLine()
      while (line != null) {
        lines = lines :+ line
        line = reader.readLine()
      }
      lines
    } finally {
      fileReader.close()
    }
  }
}

object Context {
  implicit def file(f: File) = new MyFiles(f)
}

object MyFiles {

  import Context._

  val f = new File("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/log4j.properties")

  f.lines
}