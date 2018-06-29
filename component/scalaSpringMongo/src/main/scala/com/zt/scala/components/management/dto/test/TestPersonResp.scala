package com.zt.scala.components.management.dto.test

import com.ecfront.dew.common.Page
import com.zt.scala.domain.{SecurityBasicDTO, TestPersonEntity}
import com.zt.scala.page.CommonConverter
import io.swagger.annotations.{ApiModel, ApiModelProperty}

import scala.beans.BeanProperty

@ApiModel(value = "TestPersonResp")
class TestPersonResp extends SecurityBasicDTO {

  @BeanProperty
  @ApiModelProperty(value = "用户识别码", name = "code", dataType = "string")
  var code: String = _
  @BeanProperty
  @ApiModelProperty(value = "用户名称", name = "name", dataType = "string")
  var name: String = _
  @BeanProperty
  @ApiModelProperty(value = "用户年龄", name = "age", dataType = "integer")
  var age: Int = _
  @BeanProperty
  @ApiModelProperty(value = "用户地址", name = "address", dataType = "string")
  var address: String = _
  @BeanProperty
  @ApiModelProperty(value = "用户性别", name = "gender", dataType = "boolean")
  var gender: Boolean = _
}

object TestPersonResp {

  implicit def convert(ori: TestPersonEntity): TestPersonResp = {
    CommonConverter.convert[TestPersonResp](ori)
  }

  //隐式的将domain.Page[AccessDataEntity] 转成 dew.Page[TestPersonResp]
  implicit def convertPage(ori: org.springframework.data.domain.Page[TestPersonEntity]): Page[TestPersonResp] = {
    CommonConverter.convertPage[TestPersonResp, TestPersonEntity](ori)
  }
}
