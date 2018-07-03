package com.zt.scala.fortest

import java.util.concurrent.atomic.AtomicInteger

import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global


object TestMultiFor extends App {

  val count = new AtomicInteger(0)
  for {
    low <- getLowerList()
    upper <- toUpperList(low)
    a <- print(low)
    b <- print(upper)
  } println(a + " " + b)


  def getLowerList(): Future[List[String]] = {
    val p = Promise[List[String]]
    p.success(List("a", "b", "c"))
    p.future
  }

  def toUpperList(low: List[String]): Future[List[String]] = {
    val p = Promise[List[String]]
    p.success(low.map(_.toUpperCase))
    p.future
  }

  def print(string: List[String]): Future[Long] = {
    count.incrementAndGet()
    val p = Promise[Long]
    p.success(count.longValue())
    println(string)
    p.future
  }


  Thread.sleep(10000)
}
