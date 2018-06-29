package com.zt.scala.components.management

import java.util.concurrent.{ExecutorService, Executors}
import java.util.{Date, UUID}

import com.ecfront.dew.common.$
import com.zt.scala.domain.TestPersonEntity

object Test extends App {
  val executors: ExecutorService = Executors.newFixedThreadPool(10);


  for (i <- 1 to 10) {
   executors.submit(
     new Runnable {
       override def run(): Unit = {
         while (true) {
           val testPersonEntity = new TestPersonEntity
           testPersonEntity.setId(UUID.randomUUID().toString)
           testPersonEntity.setName("用户 " + i)
           testPersonEntity.setAge(20 + i)
           testPersonEntity.setGender(if (i % 2 == 0) true else false)
           testPersonEntity.setCode(UUID.randomUUID().toString)
           testPersonEntity.setAddress("hangzhou")
           testPersonEntity.setCreateTime(new Date().getTime)
           testPersonEntity.setUpdateTime(new Date().getTime)
           testPersonEntity.setCreateUser("zhangtong")
           testPersonEntity.setDelFlag(false)
           testPersonEntity.setEnabled(true)
           $.http.post("http://localhost:8080/test", testPersonEntity)
         }
       }
     }
   )
  }

  executors.shutdown()
}
