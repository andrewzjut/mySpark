package com.zt.scala.myimplicit

import java.io._

object Test {

  implicit class Files(file: File) {
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

    def fileName: Unit = println(file.getName)
  }
}


object Test2 {
  def main(args: Array[String]): Unit = {
    import Test._
    val file: File = new File("/Users/zhangtong/IdeaProjects/git/mySpark/component/practise/src/main/resources/log4j.properties")
    file.lines foreach println
    file.fileName


    try {
      val f = new FileReader("input.txt")
    } catch {
      case ex: FileNotFoundException => {
        println("Missing file exception")
      }
      case ex: IOException => {
        println("IO Exception")
      }
    }

  }
}
