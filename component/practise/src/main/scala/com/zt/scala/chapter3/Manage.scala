package com.zt.scala.chapter3

import java.io.{BufferedInputStream, FileInputStream}

import scala.reflect.io.File
import scala.util.control.NonFatal

object Manage {
  def apply[R <: {def close() : Unit}, T](resource: R)(f: R => T) = {
    var res: Option[R] = None
    try {
      res = Some(resource)
      f(res.get)
    } catch {
      case NonFatal(ex) => println(s"Non Fatal exception! $ex")
    } finally {
      if (res != None) {
        println(s"Closing resource...")
        res.get.close()
      }
    }
  }
}

object TryCatchARM {
  def main(args: Array[String]): Unit = {
    args foreach (arg => getSize(arg))
  }

  import scala.io.Source

  def countLines(fileName: String): Unit = {
    println()
    Manage(Source.fromFile(fileName)) {
      source =>
        val size = source.getLines().size
        println(s"file $fileName hase $size lines")
        if (size > 20) throw new RuntimeException("Big File")
    }
  }

  def getSize(fileName: String): Unit = {
    Manage(new FileInputStream(new java.io.File(fileName))) {
      in: FileInputStream =>
        val stream = new BufferedInputStream(in)
        println(stream.available())
    }
  }
}
