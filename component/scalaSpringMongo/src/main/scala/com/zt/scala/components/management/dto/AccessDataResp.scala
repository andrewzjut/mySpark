package com.zt.scala.components.management.dto

import com.ecfront.dew.common.Page
import com.zt.scala.domain.{AccessDataEntity, SecurityBasicDTO}
import com.zt.scala.page.CommonConverter
import io.swagger.annotations.{ApiModel, ApiModelProperty}

import scala.beans.BeanProperty

@ApiModel(value = "AccessDataResp")
class AccessDataResp extends SecurityBasicDTO {

  @BeanProperty
  @ApiModelProperty(value = "dmp.source.{code},dmp.format.{code}", name = "code", dataType = "string")
  var code: String = _
  /**
    * 任务名称
    */
  @BeanProperty
  @ApiModelProperty(value = "任务名称", name = "name", dataType = "string")
  var name: String = _
  /**
    * 版本号
    */
  @BeanProperty
  @ApiModelProperty(value = "版本号", name = "version", dataType = "long")
  var version: Long = 0L

  @BeanProperty
  @ApiModelProperty(value = "批处理数据大小", name = "batchSize")
  var batchSize = 1000

  @BeanProperty
  @ApiModelProperty(value = "描述", name = "desc", dataType = "string")
  var desc: String = _

  @BeanProperty
  @ApiModelProperty(value = "是否进行RDS存储", name = "saveToRDS", dataType = "boolean")
  var saveToRDS = false

  @BeanProperty
  @ApiModelProperty(value = "是否进行ODS存储", name = "saveToODS", dataType = "boolean")
  var saveToODS = false
}

object AccessDataResp {

  implicit def convert(ori: AccessDataEntity): AccessDataResp = {
    CommonConverter.convert[AccessDataResp](ori)
  }

  //隐式的将domain.Page[AccessDataEntity] 转成 dew.Page[AccessDataResp]
  implicit def convertPage(ori: org.springframework.data.domain.Page[AccessDataEntity]): Page[AccessDataResp] = {
    CommonConverter.convertPage[AccessDataResp, AccessDataEntity](ori)
  }
}