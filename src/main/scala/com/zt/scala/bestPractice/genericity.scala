package com.zt.scala.bestPractice


import java.net.{ServerSocket, Socket}
import java.util.concurrent.{Callable, ExecutorService, Executors, FutureTask}


class genericity {

}


trait Mammal

trait Dog extends Mammal

trait Cat extends Mammal

object test1 extends App {


  val votes = Seq(("scala", 1), ("java", 4), ("scala", 10), ("scala", 1), ("python", 10))
  val orderedVotes = votes
    .groupBy(_._1)
    .map { case (which, counts) =>
      (which, counts.foldLeft(0)(_ + _._2))
    }.toSeq
    .sortBy(_._2)
    .reverse

  println(orderedVotes)


  /*
  * scala 1
  * scala 10    scala 0 + 1 + 10 + 1  => scala 12
  * scala 1
  *
  * java 4                               java  4                    =>  (java 4)  (python 10)  (scala 12)  .reverse
  *
  * python 10                            python 10
  * */

  import scala.collection.JavaConverters._

  val list: java.util.List[Int] = Seq(1, 2, 3, 4).asJava
  val buffer: scala.collection.mutable.Buffer[Int] = list.asScala


  val hello = new Thread(new Runnable {
    def run() {
      println("hello world")
    }
  })

  hello.start()


  class NetworkService(port: Int, poolSize: Int) extends Runnable {
    val serverSocket = new ServerSocket(port)

    def run() {
      while (true) {
        // This will block until a connection comes in.
        val socket = serverSocket.accept()
        (new Thread(new Handler(socket))).start()
      }
    }
  }

  class Handler(socket: Socket) extends Runnable {
    def message = (Thread.currentThread.getName() + "\n").getBytes

    def run() {
      socket.getOutputStream.write(message)
      socket.getOutputStream.close()
    }
  }

  //  new NetworkService(2020, 2).run

  val pool: ExecutorService = Executors.newFixedThreadPool(10)

  val future = new FutureTask[String](new Callable[String] {
    def call(): String = {
      println("task")
      "task"
    }
  })
  pool.execute(future)
  pool.shutdown()
}