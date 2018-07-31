package com.zt.scala.chapter5


class Animal {
  def hello(): Unit = println("i'm animal ")
}

class Dog {
  def bark(): Unit = println("i'm dog ")
}


class Cat {
  def miao(): Unit = println("i'm dog ")
}

object Dog {
  implicit def convert(dog: Dog): Animal = new Animal  // 隐式的将Dog 转 Animal

  implicit def convertList(animal: List[Animal]): List[Dog] = {  // 隐式的将List[Dog] 转 List[Animal]
    List[Dog](new Dog, new Dog)
  }
  implicit def convertCat2Dog(cat: Cat): Dog = new Dog
}


object Test2 {
  def main(args: Array[String]): Unit = {
    val dog = new Dog
    dog.hello()
    a(List[Animal](new Animal))  //注意必须带上[Animal]
    val cat: Cat = new Cat
    cat2Dog(cat).bark()

    import Dog._
    cat.bark()

  }

  def a(list: List[Dog]): Unit = println("list of dog")
  def cat2Dog(cat: Cat): Dog = new Cat  //返回值为Dog 进行隐式转换
}