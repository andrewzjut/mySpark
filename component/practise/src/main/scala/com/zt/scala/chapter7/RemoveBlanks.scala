package com.zt.scala.chapter7

import scala.io.Source

object RemoveBlanks {

  def apply(path: String, compressWhiteSpace: Boolean = false): Seq[String] =
    for {
      line <- Source.fromFile(path).getLines().toSeq
      if line.matches("""^\s*$""") == false
      line2 = if (compressWhiteSpace) line replaceAll("\\s+", " ") else line
    } yield line2

  def main(args: Array[String]): Unit = {
    for {
      path2 <- args
      (compress, path) = if (path2 startsWith "-") (true, path2.substring(1)) else (false, path2)
      line <- apply(path, compress)
    } println(line)

    val states = List("Alabama", "Alaska", "Virginia", "Wyoming")

    states flatMap (_.toSeq withFilter (_.isLower) map (c => s"$c-${c.toUpper}"))
    states.flatMap(_.filter(_.isLower).map(c => s"$c-${c.toUpper}"))
    states.flatMap(c => c).filter(_.isLower).map(c => s"$c-${c.toUpper}")

    val map = Map("one" -> 1, "two" -> 2)
    val list = for {
      (key, value) <- map
      i10 = value + 10
    } yield i10

    val results: Seq[Option[Int]] = Vector(Some(10), None, Some(20))

    val results2 = for {
      Some(i) <- results
    } yield 2 * i

    val results2b = for {
      Some(i) <- results withFilter {
        case Some(i) => true
        case None => false
      }
    } yield (2 * i)


  }
}
