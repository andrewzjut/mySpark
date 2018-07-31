package com.zt.scala.chapter3
//枚举
object Breed extends Enumeration {
  //  type Breed = Value
  val doberman = Value("Doberman Pinscher")
  val yorkie = Value("Yorkshire Terrier")
  val scottie = Value("Scottish Terrier")
  val dane = Value("Great Dane")
  val portie = Value("Portuguese Water Dog")
}

object WeekDay extends Enumeration {
  type WeekDay = Value
  val Mon, Tue, Wed, Thu, Fri, Sat, Sun = Value
}

object Test extends App {

  import Breed._
  import WeekDay._

  println("ID\tBreed")
  for (breed <- Breed.values) println(s"${breed.id}\t$breed")

  // 打印 犬列表
  println("\nJust Terriers:")
  Breed.values filter (_.toString.endsWith("Terrier")) foreach println


  def isTerrier(b: Breed.Value) = b.toString.endsWith("Terrier")

  println("\nTerriers Again??")
  Breed.values filter isTerrier foreach println

  def isWorkingDay(d: WeekDay) = !(d == Sat || d == Sun)

  WeekDay.values filter isWorkingDay foreach println
}