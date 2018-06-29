package com.zt.scala.chapter6

import scala.annotation.tailrec

object Chapter6 extends App {

  def factorial(i: Int): Long = {
    def fact(i: Int, accumulator: Int): Long = {
      if (i <= 1) accumulator
      else fact(i - 1, i * accumulator)
    }

    fact(i, 1)
  }

  println(factorial(3))

  val a = (1 to 10) filter (_ % 2 == 0) map (_ * 2) reduce (_ + _)

  println(a)


  val factor = 2
  val multiplier = (i: Int) => i * factor
  (1 to 10) filter (_ % 2 == 0) map multiplier reduce (_ * _)

  def cat1(s1: String)(s2: String) = s1 + s2

  def hello = cat1("hello: ") _

  println(hello("world"))

  //部分应用函数
  def cat2(s1: String) = (s2: String) => s1 + s2

  def hello2 = cat2("hello: ")

  println(hello2("world"))
  println(cat2("hello + ")("world"))

  //偏函数
  val finicky: PartialFunction[String, String] = {
    case "finicky" => "FINICKY"
  }

  val finickyOption = finicky.lift

  println(finicky("finicky"))

  println(finickyOption("dd"))

  def map[A, B](f: A => B)(list: List[A]): List[B] = list map f

  def map2[A, B](f: A => B)(a: A): B = f(a)

  val intToString = (i: Int) => s"N=$i"

  val flist = map(intToString) _

  val list = flist(List(1, 2, 3, 4))

  def seqOp(a: Int, b: Int): Int = {
    println("seqOp: " + a + "\t" + b)
    math.min(a, b)
  }

  def combOp(a: Int, b: Int): Int = {
    println("combOp: " + a + "\t" + b)
    a + b
  }

  val z = List(1, 2, 3, 4, 5, 6).aggregate(scala.collection.mutable.Set.empty[Int])(_ += _ % 6, _ ++ _)
  println(z)
  val stringBuilder: StringBuilder = new StringBuilder
  val res2 = List('a', 'b', 'c', 'd', 'e', 'f').map(x => x.toString).aggregate(stringBuilder)(
    (stringBuilder, number) => stringBuilder.append(number.toUpperCase()),
    _.append(_)
  )
  println(res2)

  val res = List(1, 2, 3, 4, 5, 6, 7, 8, 9).aggregate((0, 0))(
    (acc, number) => (acc._1 + number, acc._2 + 1),
    (part1, part2) => (part1._1 + part2._1, part1._2 + part2._2)
  )
  println(res)

  val inverse: PartialFunction[Double, Double] = {
    case d if d != 0.0 => 1.0 / d
  }

}


