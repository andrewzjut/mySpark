package com.zt.scala

import java.util

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object scalaCollectionJavaTrans extends App {

  /*
Iterator <=> java.util.Iterator
Iterator <=> java.util.Enumeration
Iterable <=> java.lang.Iterable
Iterable <=> java.util.Collection
mutable.Buffer <=> java.util.List
mutable.Set <=> java.util.Set
mutable.Map <=> java.util.Map
mutable.ConcurrentMap <=> java.util.concurrent.ConcurrentMap
  */

  val javaList: java.util.List[Int] = new util.ArrayList[Int]();
  for (i <- 0 to 2) {
    javaList.add(i)
  }
  val scalaList: List[Int] = List.apply(0, 1, 2)

  scalaList.foreach(a => println(a))
  //  javaList.forEach(a => println(a))  //这个语句是错误的  因为javaList 是java类型  不能使用scala的操作方式

  /*  import scala.collection.JavaConversions._

    javaList.foreach(a => println(a))
    //这个语句是正确的 java.util.List 转成了 scala 类型的 list*/

  val scalaIterator: Iterator[Int] = Iterator(1, 2, 3, 4, 5)

  val javaIterator: java.util.Iterator[Int] = javaList.iterator()

  //  javaIterator.drop(0)  //这个语句是错误的  drop 是scala Iterator 独有的
  /*
    import scala.collection.JavaConversions._

    javaIterator.drop(0)
    //这个语句是正确的 java.util.Iterator 转成了 scala 类型的 Iterator
    */


  val scalaBuffer: ArrayBuffer[Int] = ArrayBuffer(0, 1, 2)
  val javaBuffer: java.util.List[Int] = javaList
  javaBuffer.add(3)
  //  scalaBuffer.add(3) //这个语句是错误的  add 是 java List 独有的
  /*  import scala.collection.JavaConversions._

    scalaBuffer.add(3)

    //这个语句是正确的 ArrayBuffer 转成了 java 类型的 List
    */


  val scalaSet: mutable.Set[Int] = mutable.Set(0, 1, 2)
  val javaSet: java.util.Set[Int] = new util.HashSet[Int]()
  for (i <- 0 to 2) {
    javaSet.add(i)
  }

  //  javaSet.+=(3) //这个语句是错误的  += 是 scala Set 独有的
  /*  import scala.collection.JavaConversions._

    javaSet.+=(3)  //正确

    */

  val scalaMap: Map[Int, Int] = Map(0 -> 0, 1 -> 1, 2 -> 2)
  val javaMap: java.util.Map[Int, Int] = new util.HashMap();
  for (i <- 0 to 2) {
    javaMap.put(i, i)
  }

  //    javaMap.toList //这个语句是错误的  toList 是 scala Map 独有的
  //  scalaMap.toList.foreach(kv => println(kv._1 + " " + kv._2))

/*  import scala.collection.JavaConversions._

  javaMap.toList.foreach(kv => println(kv._1 + " " + kv._2))*/
}
