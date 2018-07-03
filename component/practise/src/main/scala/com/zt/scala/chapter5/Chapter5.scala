package com.zt.scala.chapter5

import scala.collection.mutable.ArrayBuffer

object Chapter5 extends App {
  //implicit 使用隐式能够减少代码，能够向已有的类型中注入新的方法
  def calcTax(amount: Float)(implicit rate: Float): Float = amount * rate

  implicit val currentTaxRate = 0.08f
  val tax = calcTax(50000F)
  println(tax)


  val list = MyList(List(1, 3, 5, 2, 4))
  list sortBy1 (i => -i) foreach println
  list sortBy2 (i => -i) foreach println


  val buffer: ArrayBuffer[Int] = ArrayBuffer(1, 2)
  buffer.+=(3)
  println(buffer)

}

case class MyList[A](list: List[A]) {
  def sortBy1[B](f: A => B)(implicit ordering: Ordering[B]): List[A] =
    list.sortBy(f)(ordering)

  def sortBy2[B: Ordering](f: A => B): List[A] =
    list.sortBy(f)(implicitly[Ordering[B]])
}