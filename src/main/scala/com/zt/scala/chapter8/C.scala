package com.zt.scala.chapter8

object C {

  val sex = "Male"


  def main(args: Array[String]): Unit = {
    val zhang = new C()
    val wang = new C("wang", 20)
    println(wang.name)
    zhang.age = 21
    println(zhang.age)
    println(C.sex)

  }
}

class C(var name: String = "zhang", var age: Int = 0) {

  override def toString = s"C($name, $age)"
}
