package com.zt.scala.components.management

import java.util.{Date, UUID}

import com.ecfront.dew.common.{$, Resp}
import com.tairanchina.csp.dew.Dew
import com.zt.scala.domain.TestPersonEntity
import com.zt.scala.utils.JsonHelper
import org.junit.{After, Before, Test}
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.data.domain.Page
import org.springframework.test.context.junit4.SpringRunner
import org.junit.Assert._

@RunWith(classOf[SpringRunner])
@SpringBootTest(classes = Array(classOf[Dew], classOf[ManagementApplication]), webEnvironment = WebEnvironment.DEFINED_PORT)
class ManagementApplicationTest {


  @Test
  def add(): Unit = {


    for (j <- 1 to 10) {
      new Runnable {
        override def run(): Unit = {
          for (i <- 1 to 10) {
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
      }.run()
    }
  }

  //  @Test
  def test(): Unit = {
    query()
    //    deleteByCode
  }


  def query(): Unit = {
    val respString = $.http.get("http://localhost:8080/test")
    val resp = $.json.toObject(respString, classOf[Resp[Page[TestPersonEntity]]])
    assertTrue(resp.ok())
    println($.json.toJsonString(resp))

  }


  def deleteByCode: Unit = {
    for (i <- 1 to 100) {
      val respString = $.http.delete("http://localhost:8080/test/" + i)
      val resp = $.json.toObject(respString, classOf[Resp[Boolean]])
      assertTrue(resp.getBody)
    }
  }

  //  @After
  def physicalDel: Unit = {
    for (i <- 1 to 100) {
      val respString = $.http.delete("http://localhost:8080/test/physical/" + i)
      val resp = $.json.toObject(respString, classOf[Resp[Int]])
    }
  }


}

