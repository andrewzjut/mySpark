package com.zt.scala.chapter4

import com.zt.scala.chapter2.Point
import com.zt.scala.chapter4.Chapter4.{age, name}
import com.zt.scala.chapter4.Op.Op

import scala.annotation.tailrec

object Chapter4 extends App {
  val bools = Seq(true, false)
  for (bool <- bools) {
    bool match {
      case true => println("true")
      case false => println("false")
    }
  }

  for {
    x <- Seq(1, 2, 3.7, "one", "two", true, Point(1, 2))
  } {
    x match {
      case 1 => println("int 1")
      case i: Int => println("other int: " + i)
      case d: Double => println("double: " + d)
      case "one" => println("string one")
      case s: String => println("other string: " + s)
      case b: Boolean => println("boolean:  " + b)
      case unexpected => println("unexpected value: " + unexpected)
    }
  }

  def checkY(y: Int) = {
    for {
      x <- Seq(99, 100, 101)} {
      val str = x match {
        case `y` => "found y!"
        case i: Int => "int: " + i
      }
      println(str)
    }
  }

  checkY(100)

  val nonEmptySeq = Seq(1, 2, 3, 4, 5)

  val emptySeq = Seq.empty[Int]

  nonEmptySeq +: emptySeq
  emptySeq +: nonEmptySeq


  nonEmptySeq :: List(1, 2)

  val nonEmptyList = List(1, 2, 3, 4, 5)

  val emptyList = Nil

  val nonEmptyVector = Vector(1, 2, 3, 4, 5)

  val emptyVector = Vector.empty[Int]

  val nonEmptyMap = Map("one" -> 1, "two" -> 2, "three" -> 3)

  val emptyMap = Map.empty[String, Int]

  def seqToString[T](seq: Seq[T]): String = seq match {
    case head +: tail => s"$head +: " + seqToString(tail)
    case Nil => "Nil"
  }

  def reverseSeqToString[T](l: Seq[T]): String = l match {
    case prefix :+ end => reverseSeqToString(prefix) + s" :+ $end"
    case Nil => "Nil"
  }

  nonEmptyList :+ (1, 2)
  //res9: List[Any] = List(1, 2, 3, 4, 5, (1,2))


  (1, 2) +: nonEmptyList
  //res10: List[Any] = List((1,2), 1, 2, 3, 4, 5)

  for (seq <- Seq(nonEmptySeq, emptySeq, nonEmptyList, emptyList,
    nonEmptyVector, emptyVector, nonEmptyMap.toSeq, emptyMap.toSeq)) {
    println(seqToString(seq))
  }

  println(nonEmptyList.reverse.head)

  nonEmptySeq +: List(1, 2) foreach (x => print(x + ";"))
  println()
  List(1, 2, 3, 4, 5).::(1, 2) foreach (x => print(x + ";"))
  println()
  nonEmptySeq ++: List(1, 2) foreach (x => print(x + ";"))


  /*
  scala> "A"::"B"::Nil
  res0: List[String] = List(A, B)

  scala> "A"+:"B"+:Nil
  res1: List[String] = List(A, B)

  scala> Nil:+"A":+"B"
  res2: List[String] = List(A, B)

  scala> res0 ++ res1
  res3: List[String] = List(A, B, A, B)

  scala> res0 ::: res1
  res4: List[String] = List(A, B, A, B)

  scala> res0 :: res1
  res5: List[java.io.Serializable] = List(List(A, B), A, B)
  */

  println()

  val langs = Seq(
    ("Scala", "Martin", "Odersky"),
    ("Clojure", "Rich", "Hickey"),
    ("Lisp", "John", "McCarthy")
  )

  for (tuple <- langs) {
    tuple match {
      case ("Scala", _, _) => println("found scala")
      case (lang, first, last) => println(s"Found other language: $lang ($first, $last)")
    }
  }

  for (i <- Seq(1, 2, 3, 4)) {
    i match {
      case _ if i % 2 == 0 => println(s"even: $i")
      case _ => println(s"odd:  $i")
    }
  }

  val alice = Person("Alice", 25, Address("1 Scala Lane", "Chicago", "USA"))
  val bob = Person("Bob", 29, Address("2 Java Ave.", "Miami", "USA"))
  val charlie = Person("Charlie", 32, Address("3 Python Ct.", "Boston", "USA"))


  for (person <- Seq(alice, bob, charlie)) {
    person match {
      case Person("Alice", 25, Address(_, "Chicago", _)) => println("Hi Alice!")
      case Person("Bob", 29, Address("2 Java Ave.", "Miami", "USA")) => println("Hi Bob!")
      //调用 Person类伴生对象的unapply()方法
      case Person(name, age, _) => println(s"Who are you, $age year-old person named $name?")
    }
  }


  val itemsCosts = Seq(("Pencil", 0.52), ("Paper", 1.35), ("Notebook", 2.43))
  val itemsCostsIndices = itemsCosts.zipWithIndex

  for (itemsCostsIndex <- itemsCostsIndices) {
    itemsCostsIndex match {
      case ((item, cost), index) => println(s"$index: $item costs $cost each")
    }
  }

  val with1: With[String, Int] = With("Foo", 1)
  val with2: String With Int = With("Bar", 2)

  Seq(with1, with2) foreach { w =>
    w match {
      case s With i => println(s"$s with $i")
      case _ => println(s"Unknown: $w")
    }
  }

  def windows[T](seq: Seq[T]): String = seq match {
    case Seq(head1, head2, _*) => s"($head1, $head2), " + windows(seq.tail)
    case Seq(head, _*) => s"($head, _), " + windows(seq.tail)
    case Nil => "Nil"
  }

  for (seq <- Seq(nonEmptyList, emptyList, nonEmptyMap.toSeq)) {
    println(windows(seq))
  }

  def windows2[T](seq: Seq[T]): String = seq match {
    case head1 +: head2 +: tail => s"($head1, $head2), " + windows2(seq.tail)
    case head +: tail => s"($head, _), " + windows2(tail)
    case Nil => "Nil"
  }

  for (seq <- Seq(nonEmptyList, emptyList, nonEmptyMap.toSeq)) {
    println(windows2(seq))
  }


  import Op._

  val wheres = Seq(
    WhereIn("state", "IL", "CA", "VA"),
    WhereOp("state", EQ, "IL"),
    WhereOp("name", EQ, "Buck Trends"),
    WhereOp("age", GT, 29)
  )

  for (where <- wheres) {
    where match {
      case WhereIn(col, val1, vals@_*) =>
        val valStr = (val1 +: vals).mkString(", ")
        println(s"WHERE $col IN ($valStr)")
      case WhereOp(col, op, value) => println(s"WHERE $col $op $value")
      case _ => println(s"ERROR: Unknown expression: $where")
    }
  }

  def echo(strings: String*): Unit = {
    for (arg <- strings) println(arg)
  }

  echo("hello", "world!")
  val arr = Array("What's", "up", "doc?")
  echo(arr: _*)

  val BookExtractorRE = """Book: title=([^,]+),\s+author=(.+)""".r
  val MagazineExtractorRE = """Magazine: title=([^,]+),\s+issue=(.+)""".r

  val catalog = Seq(
    "Book: title=Programming Scala Second Edition, author=Dean Wampler",
    "Magazine: title=The New Yorker, issue=January 2014",
    "Unknown: text=Who put this here??"
  )

  for (item <- catalog) {
    item match {
      case BookExtractorRE(title, author) =>
        println(s"""Book "$title", written by $author""")
      case MagazineExtractorRE(title, issue) =>
        println(s"""Magazine "title", issue $issue""")
      case entry => println(s"Unrecognized entry: $entry")
    }
  }

  for (person <- Seq(alice, bob, charlie)) {
    person match {
      case p@Person("Alice", 25, address) => println(s"Hi Alice! $p")
      case p@Person("Bob", 29, a@Address(street, city, country)) =>
        println(s"Hi ${p.name}! age ${p.age}, in ${a.city}")
      case p@Person(name, age, _) =>
        println(s"Who are you, $age year-old person named $name? $p")
    }
  }


  val Person(name, age, Address(_, state, _)) =
    Person("Dean", 29, Address("1 scala way", "ca", "usa"))

  println(name)

  val head +: tail1 = List(1, 2, 3)
  println(head)

  val head1 +: head2 +: tail2 = Vector(1, 2, 3)
  println(head2)

  val Seq(a, b, c, d) = List(1, 2, 3, 4)

  def sum_count(ints: Seq[Int]) = (ints.sum, ints.size)

  val (sum, count) = sum_count(List(1, 2, 3, 4, 5, 6))

  val as = Seq(
    Address("1 Scala Lane", "Anytown", "USA"),
    Address("2 Clojure Lane", "Othertown", "USA"))

  val ps = Seq(
    Person2("Buck Trends", 29),
    Person2("Clo Jure", 28))
  val pas = ps zip as

  pas.map {
    person =>
      person match {
        case (Person2(name, age), Address(street, city, country)) => println(s"$name (age: $age) lives at $street, $city, in $country")
      }
  }

  pas.map {
        case (Person2(name, age), Address(street, city, country)) => println(s"$name (age: $age) lives at $street, $city, in $country")
  }

}


case class Address(street: String, city: String, country: String)

case class Person(name: String, age: Int, address: Address)

case class Person2(name: String, age: Int)

/*
object Person {
       def apply(name: String, age: Int, address: Address) =
         new Person(name, age, address)
       def unapply(p: Person): Option[Tuple3[String,Int,Address]] =
         Some((p.name, p.age, p.address))
}

*/

case class With[A, B](a: A, b: B)

case class WhereOp[T](columnName: String, op: Op, value: T)

case class WhereIn[T](columnName: String, val1: T, vals: T*)