package com.zt.scala.components.management.controller.test

import com.ecfront.dew.common.{Page, Resp}
import com.zt.scala.components.management.dto.test.TestPersonResp
import com.zt.scala.components.management.service.test.TestPersonService
import com.zt.scala.domain.TestPersonEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._


@RestController
@RequestMapping(Array("test"))
class TestPersonController @Autowired()(val testPersonService: TestPersonService) {

  @GetMapping
  def getAll(@RequestParam(value = "pageNum", defaultValue = "1") pageNum: Int,
             @RequestParam(value = "pageSize", defaultValue = "20") pageSize: Int): Resp[Page[TestPersonResp]] = {
    testPersonService.findAllTestPerson(pageNum, pageSize)
  }

  @GetMapping(Array("/nameLike/{name}"))
  def getByNameLike(@RequestParam(value = "pageNum", defaultValue = "1") pageNum: Int,
                    @RequestParam(value = "pageSize", defaultValue = "1") pageSize: Int,
                    @PathVariable name: String): Resp[Page[TestPersonResp]] = {
    testPersonService.findByNameLike(name, pageNum, pageSize)
  }

  @PostMapping
  def addOne(@RequestBody testPersonEntity: TestPersonEntity): Resp[Void] = {
    testPersonService.addTestPerson(testPersonEntity)
  }

  @PostMapping(Array("bulk/{code}"))
  def bulkOp(@PathVariable code: String): Resp[Void] = {
    testPersonService.bulkOp(code)
    Resp.success(null)
  }


  @DeleteMapping(Array("{code}"))
  def deleteByCode(@PathVariable code: String): Resp[Boolean] = {
    testPersonService.delFlagTestPerson(code)
  }

  @DeleteMapping(Array("/physical/{code}"))
  def delete(@PathVariable code: String): Resp[Long] = {
    testPersonService.delByCode(code)
  }

  @DeleteMapping(Array("/{name}"))
  def deleteByName(@PathVariable name: String): List[TestPersonEntity] = {
    testPersonService.delByName(name)
  }

}
