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


  List(1, 3, 5, 2, 4).sortBy(i => -i) foreach println


}

case class MyList[A](list: List[A]) {
  def sortBy1[B](f: A => B)(implicit ordering: Ordering[B]): List[A] =
    list.sortBy(f)(ordering)

  def sortBy2[B: Ordering](f: A => B): List[A] =
    list.sortBy(f)(implicitly[Ordering[B]])
}

class A {
  implicit class MyList2(list: List[Int]) {
    def length2: Long = {
      list.length
    }
  }

  implicit class SayhiImpl(ivalue: Int) {
    val value: Int = ivalue

    def sayhi = println(s"Hi $value!")
  }

}

object B {
  def main(args: Array[String]): Unit = {
    //1 使用场景一：隐式参数
    def sayHello(age: Int)(implicit name: String) = println("my name is:" + name + ",my age is:" + age)

    implicit val name = "zhangtong"
    sayHello(23)

    //使用场景二：类型匹配
    import scala.language.implicitConversions
    implicit def int2Range(num: Int): Range = 1 to num

    def spreadNum(range: Range): String = range.mkString(",")

    println(spreadNum(5))

    //使用场景三：类型增强
    implicit def man2SuperMan(man: Man): SuperMan = new SuperMan(man.name)

    val man = new Man("zhangtong")
    println(man.getClass)
    man.fly

    implicit class MyImplicitTypeConversion(val man: Man) {
      def fly2 = println("you can fly")
    }
    man.fly2
  }
}


class Man(val name: String)

class SuperMan(val name: String) {
  def fly = println("you can fly")
}



