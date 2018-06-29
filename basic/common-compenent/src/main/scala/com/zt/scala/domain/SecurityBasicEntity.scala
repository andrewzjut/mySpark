package com.zt.scala.domain

import io.swagger.annotations.{ApiModel, ApiModelProperty}

import scala.beans.BeanProperty

@SerialVersionUID(5322655938021210759L)
@ApiModel(value = "SecurityBasicEntity")
class SecurityBasicEntity extends IdBasicEntity {
  // 创建人
  @BeanProperty
  @ApiModelProperty(value = "创建人", name = "createUser", dataType = "string")
  var createUser: String = _
  // 创建时间
  @BeanProperty
  @ApiModelProperty(value = "创建时间", name = "createTime")
  var createTime: Long = _
  // 更新人
  @BeanProperty
  @ApiModelProperty(value = "更新人", name = "updateUser", dataType = "string")
  var updateUser: String = _
  // 更新时间
  @BeanProperty
  @ApiModelProperty(value = "更新时间", name = "updateTime")
  var updateTime: Long = _
  // 是否删除
  @BeanProperty
  @ApiModelProperty(value = "是否删除", name = "delFlag", dataType = "boolean")
  var delFlag = false
  // 是否启用
  @BeanProperty
  @ApiModelProperty(value = "是否启用", name = "enabled", dataType = "boolean")
  var enabled = true

}
