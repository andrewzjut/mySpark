package com.zt.scala.domain

import java.util

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.mapping.Document

import scala.beans.BeanProperty

@Document(collection = "accessData")
@SerialVersionUID(-206959099000506152L)
@ApiModel(value = "AccessDataEntity")
class AccessDataEntity extends SecurityBasicEntity {
  /**
    * dmp.source.{code},dmp.format.{code}
    */
  @BeanProperty
  @ApiModelProperty(value = "dmp.source.{code},dmp.format.{code}", name = "code", dataType = "string")
  var code: String = _
  /**
    * 任务名称
    */
  @BeanProperty
  @ApiModelProperty(value = "任务名称", name = "version", dataType = "string")
  var name: String = _
  /**
    * 版本号
    */
  @BeanProperty
  @ApiModelProperty(value = "版本号", name = "version", dataType = "long")
  @Version
  var version = 0L

  @BeanProperty
  @ApiModelProperty(value = "批处理数据大小", name = "batchSize")
  var batchSize = 1000

  @BeanProperty
  @ApiModelProperty(value = "描述", name = "desc", dataType = "string")
  var desc: String = _
  /**
    * 表
    */
  @BeanProperty
  @ApiModelProperty(value = "表", name = "tables")
  var tables: util.List[AccessDataEntity.Table] = new util.ArrayList[AccessDataEntity.Table]
  @BeanProperty
  @ApiModelProperty(value = "数据指纹", name = "finger")
  var finger: util.Map[String, String] = new util.HashMap[String, String]()
  @BeanProperty
  @ApiModelProperty(value = "是否进行RDS存储", name = "saveToRDS", dataType = "boolean")
  var saveToRDS = false
  @BeanProperty
  @ApiModelProperty(value = "是否进行ODS存储", name = "saveToODS", dataType = "boolean")
  var saveToODS = false
  /**
    * 是否发送原生数据到Streaming
    */
  @BeanProperty
  @ApiModelProperty(value = "是否发送原生数据到Streaming", name = "sendOfRaw", dataType = "boolean")
  var sendOfRaw: Boolean = false
}
object AccessDataEntity {

  @SerialVersionUID(1L)
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
    var mapping: util.Map[String, String] = _
  }

}