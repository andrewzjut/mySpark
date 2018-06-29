package com.zt.scala.asyn

import java.util.concurrent.{Callable, ExecutorService, Executors, FutureTask}

import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.{Failure, Random}
//import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success


object ScalaAsyn1 extends App {


  implicit lazy val workStealingPoolExecutionContext: ExecutionContext = {
    val workStealingPool: ExecutorService = Executors.newWorkStealingPool
    ExecutionContext.fromExecutor(workStealingPool)
  }

  var start = System.currentTimeMillis()
  val value1 = {
    Thread.sleep(1000)
    1
  }
  var end = System.currentTimeMillis()
  println(s"同步等待了${end - start} 毫秒,返回值:$value1")

  start = System.currentTimeMillis()
  val value2 = Future {
    Thread.sleep(2000)
    1
  }
  end = System.currentTimeMillis()

  println(s"异步等待了${end - start} 毫秒,返回值:${value2.value}")


  import scala.concurrent.duration._
  import scala.concurrent.Await

  val resultValue = Await.result(value2, 2 seconds)
  println(s"阻塞方法,返回值:${resultValue}")


  val value3 = Future {
    val p = Promise[String]
    val random = new Random()
    if (random.nextInt % 2 == 0) p.success("success") else p.failure(null)
    p.future
  }

  value3.onComplete {
    case Success(intValue) => println("success:   " + intValue)
    case Failure(error) => println("An error has occured: " + error.getMessage)
  }


/*
  val pool: ExecutorService = Executors.newCachedThreadPool()
  val future = new FutureTask[String](new Callable[String]() {
    def call(): String = {
      Thread.sleep(2000)
      "success"
    }
  })
   pool.execute(future)
  val blockingResult = Await.result(future, 3 second)

*/

}
