package com.zt.scala.components.management.service

import java.util.{Date, UUID}

import com.ecfront.dew.common.$
import com.zt.scala.domain.TestPersonEntity

object Test extends App {
  for (i <- 0 to 10) {
    val testPersonEntity = new TestPersonEntity
    testPersonEntity.setId(UUID.randomUUID().toString)
    testPersonEntity.setName("用户 " + i)
    testPersonEntity.setAge(20 + i)
    testPersonEntity.setGender(if (i % 2 == 0) true else false)
    testPersonEntity.setCode(i + "")
    testPersonEntity.setAddress("hangzhou")
    testPersonEntity.setCreateTime(new Date().getTime)
    testPersonEntity.setUpdateTime(new Date().getTime)
    testPersonEntity.setCreateUser("zhangtong")
    testPersonEntity.setDelFlag(false)
    testPersonEntity.setEnabled(true)

    println($.json.toJson(testPersonEntity).toString)

  }

}
