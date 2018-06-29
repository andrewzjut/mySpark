package com.zt.scala.components.management.service.test

import java.util.{Date, UUID}
import java.util.concurrent.{ExecutorService, Executors}

import com.ecfront.dew.common.{$, Page, Resp}
import com.mongodb.WriteResult
import com.zt.scala.common.repository.TestPersonRepository
import com.zt.scala.components.management.dto.test.TestPersonResp
import com.zt.scala.components.management.service.BasicService
import com.zt.scala.domain.TestPersonEntity
import com.zt.scala.page.PageRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query, Update}
import org.springframework.stereotype.Service

@Service
class TestPersonService @Autowired()(implicit val mongoTemplate: MongoTemplate,
                                     val testPersonRepository: TestPersonRepository) extends BasicService {

  var executorService: ExecutorService = null;

  def addTestPerson(testPerson: TestPersonEntity): Resp[Void] = {
    val testPersonOld = getTestPersonByCode(testPerson.getCode)
    if (testPersonOld != null) {
      return Resp.badRequest("当前记录已存在")
    }
    testPersonRepository.insert(testPerson)
    return Resp.success(null)
  }

  def findAllTestPerson(pageNum: Int, pageSize: Int): Resp[Page[TestPersonResp]] = {
    Resp.success(testPersonRepository.findByDelFlag(false, new PageRequest(pageNum, pageSize)))
  }


  def delFlagTestPerson(code: String): Resp[Boolean] = {
    val update = new Update()
    update.set("delFlag", true)
    update.set("updateTime", new Date().getTime)
    val result = updateByCode(code, update, classOf[TestPersonEntity])
    Resp.success(result.isUpdateOfExisting)
  }

  private def updateByCode(code: String, update: Update, clz: Class[_]): WriteResult = {
    val query = new Query();
    query.addCriteria(new Criteria("code").is(code))
    mongoTemplate.updateFirst(query, update, clz)
  }

  private def getTestPersonByCode(code: String): TestPersonEntity = {
    testPersonRepository.getOneByCodeAndDelFlag(code, delFlag = false)
  }

  def delByCode(code: String): Resp[Long] = {
    Resp.success(testPersonRepository.deleteTestPersonEntityByCode(code))
  }

  def findByNameLike(name: String, pageNum: Int, pageSize: Int): Resp[Page[TestPersonResp]] = {
    Resp.success(testPersonRepository.findByNameLike(name, new PageRequest(pageNum, pageSize)))
  }

  def delByName(name: String): List[TestPersonEntity] = {
    testPersonRepository.deleteByName(name)
  }

  def bulkOp(code: String): Unit = {
    code match {
      case "start" =>
        executorService = Executors.newFixedThreadPool(10)
        for (i <- 0 to 10) {
          executorService.submit(new Runnable {
            override def run(): Unit = {
              {
                while (true) {
                  val testPersonEntity = new TestPersonEntity
                  testPersonEntity.setId(UUID.randomUUID().toString)
                  testPersonEntity.setName("用户")
                  testPersonEntity.setAge(20)
                  testPersonEntity.setGender(true)
                  testPersonEntity.setCode(UUID.randomUUID().toString)
                  testPersonEntity.setAddress("hangzhou")
                  testPersonEntity.setCreateTime(new Date().getTime)
                  testPersonEntity.setUpdateTime(new Date().getTime)
                  testPersonEntity.setCreateUser("zhangtong")
                  testPersonEntity.setDelFlag(false)
                  testPersonEntity.setEnabled(true)
                  testPersonRepository.insert(testPersonEntity)
                }
              }
            }
          })
        }
      case "stop" =>
        executorService.shutdownNow()
      case _ =>
    }
  }
}
