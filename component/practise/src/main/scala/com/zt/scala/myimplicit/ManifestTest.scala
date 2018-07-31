package com.zt.scala.myimplicit

import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

class MyType[T]

case class Person(var name: String, var age: Int) {
  def name_ = println(name)

  def age_ = println(age)
}

class Student {
  var name: String = _
  var age: Int = _

  override def toString: String = s"name: $name,age: $age"
}

object ManifestTest extends App {

  def arrayMake[T: ClassTag](first: T, second: T) = {
    val r = new Array[T](2)
    r(0) = first
    r(1) = second
    r
  }

  arrayMake(1, 2) foreach println

  def makeList[T](params: T*): List[T] = {
    if (params.length == 0)
      List.empty[T] // #1
    else
      params.toList
  }

  println(makeList("hello", "world"))


  //上面的写法其实与下面的写法可以认为是等价的。下面的写法是一种更原生的写法。不建议使用下面的写法
  def arrayMake2[T](first: T, second: T)(implicit m: ClassTag[T]) = {
    println(m.typeArguments)
    //打印泛型的实际类型
    println(implicitly[ClassTag[T]].runtimeClass)
    println(m.runtimeClass)

    val r = new Array[T](2)
    r(0) = first
    r(1) = second
    r
  }

  arrayMake2(1, 2) foreach println


  //implicit m: ClassTag[T] 改成implicit m: Manifest[T]也是可以的
  def manif[T](x: List[T])(implicit m: Manifest[T]) = {

    if (m <:< manifest[String])
      println("List strings")
    else
      println("Some other type")
  }

  manif(List("Spark", "Hadoop"))
  manif(List(1, 2))
  manif(List("Scala", 3))

  def manif2[T](x: List[T])(implicit m: ClassManifest[T]) = {
    //classManifest比manifest获取信息的能力更弱一点
    if (m <:< classManifest[String])
      println("List strings")
    else
      println("Some other type")
    if (m.isInstanceOf[ClassTag[String]])
      println("String")
  }

  manif2(List("Spark", "Hadoop"))
  manif2(List(1, 2))
  manif2(List("Scala", 3))

  //implicit m: ClassTag[T] 改成implicit m: TypeTag[T]也是可以的
  def manif3[T](x: List[T])(implicit m: TypeTag[T]) = {
    println(x)
  }

  manif3(List("Spark", "Hadoop"))
  manif3(List(1, 2))
  manif3(List("Scala", 3))


  //ClassTag是我们最常用的。它主要在运行时指定在编译时无法确定的类别的信息。
  // 我这边 Array[T](elems: _*) 中的下划线是占位符，表示很多元素
  def mkArray[T: ClassTag](elems: T*): Array[T] = Array[T](elems: _*)


  val m = manifest[MyType[String]]
  m match {
    case _: ClassTag[MyType[String]] => println("ok")
    case _ => println("unknown")
  }

  println(m) //myscala.scalaexercises.classtag.MyType[java.lang.String]
  val cm = classManifest[MyType[String]]
  println(cm) //myscala.scalaexercises.classtag.MyType[java.lang.String]


  /*
  其实manifest是有问题的，manifest对实际类型的判断会有误(如依赖路径)，所以后来推出了ClassTag和TypeTag，
  用TypeTag来替代manifest，用ClassTag来替代classManifest
  */
  mkArray(1, 2, 3, 4, 5).foreach(println)

  //  val ru = scala.reflect.runtime.universe

  def meth1[T: TypeTag](xs: List[T]) =
    typeTag[T].tpe match {
      case t if t <:< typeOf[String] => "list of String"
      case t if t =:= typeOf[Int] => "list of integer"
      case t if t =:= typeOf[List[String]] => "list of list of string"
      case t if t =:= typeOf[Set[List[Int]]] => "list of set of list of integer"
      case _ => "some other types"
    }

  println("=======")

  println(meth1(List(1, 2, 3)))
  println(meth1(List("a", "b")))
  println(meth1(List(List("a", "a"))))
  println(meth1(List(Set(List(1, 20)))))

  def meth2[T: WeakTypeTag](xs: List[T]) =
    weakTypeTag[T].tpe match {
      case t if t =:= typeOf[Int] => "list of integer"
      case t if t =:= typeOf[List[String]] => "list of list of string"
      case t if t =:= typeOf[Set[List[Int]]] => "list of set of list of integer"
      case _ => "some other types"
    } //> meth: [T](xs: List[T])(implicit evidence$5: ru.WeakTypeTag[T])String

  println(meth2(List(1, 2, 3)))
  println(meth2(List("a", "b")))
  println(meth2(List(List("a", "a"))))
  println(meth2(List(Set(List(1, 20)))))


  //https://www.cnblogs.com/tiger-xc/p/6006512.html


  def extract[T: ClassTag](list: List[Any]) = list.flatMap {
    case elem: T => Some(elem)
    case _ => None
  }

  println("extract >>>>>>>>>")
  extract[Int](List[Int](1, 2, 3)).foreach(x => print(x + " "))
  println()
  extract[String](List(1, "One", 2, 3, "Four", List(5))).foreach(x => print(x + " "))

  println("\nreflect >>>>>>>>..")

  def reflect[T](p: T)(implicit m: ClassTag[T]) = {

    val xx = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[T]
    // implicitly[ClassTag[T]]  == m
    val yy = m.runtimeClass.asInstanceOf[T]

    println(xx)
    println(yy)
  }

  val mm = manifest[MyType[String]]

  reflect(Person("zhang", 13))

  val person = Person("22", 4)

  def toObject[E](obj: Any, clazz: Class[E]): E = {
    val mapper = new ObjectMapper() with ScalaObjectMapper
    try {
      obj match {
        case o: String =>
          clazz match {
            case c if c == classOf[String] =>
              o.asInstanceOf[E]
            case c if c == classOf[Int] =>
              o.toInt.asInstanceOf[E]
            case c if c == classOf[Long] =>
              o.toLong.asInstanceOf[E]
            case c if c == classOf[Double] =>
              o.toDouble.asInstanceOf[E]
            case c if c == classOf[Float] =>
              o.toFloat.asInstanceOf[E]
            case c if c == classOf[Boolean] =>
              o.toBoolean.asInstanceOf[E]
            case c if c == classOf[Byte] =>
              o.toByte.asInstanceOf[E]
            case c if c == classOf[Short] =>
              o.toShort.asInstanceOf[E]
            case c if c == classOf[Void] =>
              null.asInstanceOf[E]
            case _ =>
              mapper.readValue[E](o, clazz)
          }
        case o: JsonNode => mapper.readValue(o.toString, clazz)
        case _ => mapper.readValue(mapper.writeValueAsString(obj), clazz)
      }
    } catch {
      case e: Throwable =>
        throw e
    }
  }


  def convert11[E](ori: AnyRef)(implicit m: Manifest[E]): E = {
    val dest = m.runtimeClass.newInstance().asInstanceOf[E]
    dest
  }

  def convert12[E: Manifest](ori: AnyRef): E = {
    val dest = implicitly[Manifest[E]].runtimeClass.newInstance().asInstanceOf[E]
    //implicitly[ClassTag[T]] == m
    dest
  }

  def convert21[E: ClassTag](ori: AnyRef): E = {
    val dest = implicitly[ClassTag[E]].runtimeClass.newInstance.asInstanceOf[E]
    dest
  }

  def convert22[E](ori: AnyRef)(implicit m: ClassTag[E]): E = {
    val dest = m.runtimeClass.newInstance.asInstanceOf[E]
    dest
  }

  def convert3[E: TypeTag](ori: AnyRef): E = {
    val dest = typeTag[E].mirror.runtimeClass(typeOf[E]).newInstance().asInstanceOf[E]
    dest
  }


  val student11 = convert11[Student](null)
  println(student11)

  val student12 = convert12[Student](null)
  println(student12)

  val student21 = convert21[Student](null)
  println(student21)

  val student22 = convert22[Student](null)
  println(student22)

  val student3 = convert3[Student](null)
  println(student3)

}
