package com.zt.scala.components.management.dto

import java.util

import com.fasterxml.jackson.databind.node.ArrayNode
import io.swagger.annotations.{ApiModel, ApiModelProperty}
import org.hibernate.validator.constraints.NotEmpty

import scala.beans.BeanProperty

@ApiModel(value = "AccessDataReq")
class AccessDataReq extends Serializable {

  /**
    * dmp.source.{code},dmp.format.{code}
    */
  @NotEmpty(message = "唯一识别码不能为空")
  @BeanProperty
  @ApiModelProperty(value = "dmp.source.{code},dmp.format.{code}", name = "code", dataType = "string")
  var code: String = _
  /**
    * 任务名称
    */
  @NotEmpty(message = "任务名称不能为空")
  @BeanProperty
  @ApiModelProperty(value = "任务名称", name = "name", dataType = "string")
  var name: String = _

  @BeanProperty
  @ApiModelProperty(value = "版本号", name = "version", dataType = "long")
  var version: Long = 0L

  @BeanProperty
  @ApiModelProperty(value = "批处理数据大小", name = "batchSize")
  var batchSize = 1000

  @BeanProperty
  @ApiModelProperty(value = "描述", name = "desc", dataType = "string")
  var desc: String = _
  /**
    * 表
    */
  @NotEmpty(message = "表不能为空")
  @BeanProperty
  @ApiModelProperty(value = "表", name = "tables")
  var tables = new util.ArrayList[AccessDataReq.Table]

  @BeanProperty
  @ApiModelProperty(value = "是否进行RDS存储", name = "saveToRDS", dataType = "boolean")
  var saveToRDS = false

  @BeanProperty
  @ApiModelProperty(value = "是否进行ODS存储", name = "saveToODS", dataType = "boolean")
  var saveToODS = false
}


object AccessDataReq {


  @ApiModel(value = "Table")
  class Table {
    /**
      * 表名
      */
    @BeanProperty
    @ApiModelProperty(value = "表名", name = "name", dataType = "string")
    var name: String = _
    /**
      * 字段映射
      */
    @BeanProperty
    @ApiModelProperty(value = "字段映射", name = "mapping")
    var mapping: ArrayNode = _
  }


}
