package com.zt.scala.components.management.controller

import com.ecfront.dew.common.{Page, Resp}
import com.zt.scala.components.management.dto.{AccessDataReq, AccessDataResp}
import com.zt.scala.components.management.service.AccessDataService
import io.swagger.annotations.{Api, ApiImplicitParam, ApiImplicitParams, ApiOperation}
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation._


@Api("数据接入")
@Validated
@RestController
@RequestMapping(Array("access"))
class AccessDataController @Autowired()(val accessDataService: AccessDataService) {


  @ApiOperation(nickname = "000", response = classOf[AccessDataResp], value = "通过Code获取数据接入列表")
  @ApiImplicitParams(Array(new ApiImplicitParam(name = "code", dataType = "String"),
    new ApiImplicitParam(name = "pageNum", defaultValue = "1", dataType = "int"),
    new ApiImplicitParam(name = "pageSize", defaultValue = "10", dataType = "int")))
  @GetMapping
  def findAllByPage(@RequestParam(value = "code", required = false) code: String,
                    @RequestParam(value = "pageNum", defaultValue = "1") pageNum: Int,
                    @RequestParam(value = "pageSize", defaultValue = "10") pageSize: Int):Resp[Page[AccessDataResp]] = {

    if (StringUtils.isNotEmpty(code))
      accessDataService.findByCode(code, pageNum, pageSize)
    accessDataService.findAll(pageNum, pageSize)
  }

  @ApiOperation(nickname = "000", response = classOf[AccessDataReq], value = "新增数据接入")
  @PostMapping
  def add(@Validated @RequestBody accessDataReq: AccessDataReq): Resp[Void] = {
    accessDataService.addAccessData(accessDataReq)
  }
}
