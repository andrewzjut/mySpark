package com.zt.scala.chapter2

import java.io.{BufferedInputStream, File, FileInputStream, FileNotFoundException}

import scala.annotation.tailrec
import scala.concurrent.Future
import scala.reflect.ClassTag

object Chapter2 extends App {

  //偏函数
  val pf1: PartialFunction[Any, String] = {
    case s: String => s
  }
  val pf2: PartialFunction[Any, String] = {
    case s: Double => s.toString
  }

  List("hello", 2.0, 3.14).collect(pf1).foreach(println)
  List("hello", 2.0, 3.14).collect(pf2).foreach(println)

  val pf = pf1 orElse pf2

  def tryPf(x: Any, f: PartialFunction[Any, String]): String = {
    try {
      f(x).toString
    } catch {
      case _: MatchError => "ERROR"
    }
  }

  println("===")
  List("hello", 2.0, 3.14).foreach { x =>
    println(tryPf(x, pf))
  }


  val point1 = Point(1, 1)
  val point2 = point1.shift(2, 2)
  println(point2.x + " " + point2.y)


  val circle = Circle(point1, 3);
  circle.draw(Point(1.0, 2.0))(str => println(s"ShapesDrawingActor: $str"))

  def sleep(millis: Long) = {
    Thread.sleep(millis)
  }

  def dowork(index: Int) = {
    println(s"i'm working with index:$index")
    sleep((math.random * 1000).toLong)
    index
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  (1 to 5) foreach { //5个并发
    index =>
      val future = Future { //异步执行
        dowork(index)
      }
      //隐式参数 implicit executor: ExecutionContext
      /*
    def onSuccess[U](pf: PartialFunction[T, U])(implicit executor: ExecutionContext): Unit = onComplete {
    case Success(v) =>
      pf.applyOrElse[T, Any](v, Predef.conforms[T]) // Exploiting the cached function to avoid MatchError
    case _ =>
    }
      */
      future onSuccess { //异步回调
        case answer: Int => println(s"Success! returned: $answer")
      }
      future onFailure {
        case th: Throwable => println(s"FAILURE! returned: $th")
      }
  }
  sleep(1000) // 等待足够长的时间，以确保工作线程结束。 println("Finito!")


  def factorial(i: Int): Long = {
    @tailrec
    def fact(i: Int, accumulator: Int): Long = {
      if (i <= 1) accumulator
      else fact(i - 1, i * accumulator)
    }

    fact(i, 1)

  }

  (0 to 5) foreach (i => println(factorial(i)))

  //  @tailrec
  def fibonacci(i: Int): Long = {
    if (i <= 1) 1L
    else fibonacci(i - 2) + fibonacci(i - 1)
  }

  def countTo(n: Int): Unit = {
    def count(i: Int): Unit = {
      if (i <= n) {
        println(i)
        count(i + 1)
      }
    }

    count(1)

  }

  countTo(10)


  def joiner(strings: String*): String = strings.mkString("-")

  def joiner(strings: List[String]): String = joiner(strings: _*)

  println(joiner(List("Programming", "Scala")))

  println("===")

  def makeList[T](params: T*): List[T] = {
    if (params.length == 0)
      List.empty[T] // #1
    else
      params.toList
  }

  val list: List[String] = makeList()
  println(makeList("hello", "world"))
  println(makeList())
  println(makeList(1, 2, 3))

  println(Int.MaxValue)
  println(Int.MaxValue + 1)
  println(Int.MinValue)
  println(Int.MinValue - 1)
  println(Long.MaxValue)
  println(Long.MinValue)


  def hello(name: String) =
    s"""Welcome!
       |Hello, $name!
       |* (Gratuitous Star!!)
       |We're glad you're here.
       |Have some extra whitespace.""".stripMargin

  println(hello("tong"))

  val t1: (Int, String) = (1, "one")
  val t2: Tuple2[Int, String] = (2, "Two")
  println(t2._1 + " " + t2._2)


  val stateCaptitals = Map(
    "Alabama" -> "Montgomery",
    "Alaska" -> "Juneau",
    "Wyoming" -> "Cheyenne"
  )

  println(stateCaptitals.get("Alabama"))
  println(stateCaptitals.get("NewYork").getOrElse("None exist"))

  def show(x: Option[String]): Unit = x match {
    case Some(s) => println(s)
    case None => println("None")
  }

  show(stateCaptitals.get("Alabama"))
  show(stateCaptitals.get("NewYork"))


  println(new StringBulkReader("hello world").read)
  try {
    println(new FileBulkReader(new File("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/people2.txt")).read)
  } catch {
    case ex: FileNotFoundException => println(s"file not found : ex : $ex")
    case _ => println("error")
  }
}

case class Point(x: Double = 0.0, y: Double = 0.0) {
  def shift(deltaX: Double = 0.0, deltaY: Double = 0.0) = {
    copy(x + deltaX, y + deltaY)
  }
}


abstract class shape() {
  def draw(offset: Point = Point(0, 0))(f: String => Unit): Unit = {
    f(s"draw(offset=$offset),${this.toString}")
  }
}

case class Circle(center: Point, radius: Double) extends shape

case class Rectangle(lowerLeft: Point, height: Double, width: Double) extends shape


abstract class BulkReader {
  type In
  val source: In

  def read: String
}

class StringBulkReader(val source: String) extends BulkReader {
  type In = String

  override def read: String = source
}

class FileBulkReader(val source: File) extends BulkReader {
  type In = File

  override def read: String = {
    val in = new BufferedInputStream(new FileInputStream(source))
    val numBytes = in.available();
    val bytes = new Array[Byte](numBytes)
    in.read(bytes, 0, numBytes)
    new String(bytes)
  }
}