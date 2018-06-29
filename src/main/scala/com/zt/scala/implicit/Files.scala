package com.zt.scala.`implicit`

import java.io.{BufferedReader, File, FileReader}
object Test{

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

    def fileName:Unit = println(file.getName)
  }

  def main(args: Array[String]): Unit = {
    val file:File = new File("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/log4j.properties")
    file.lines foreach println
    file.fileName

  }
}

