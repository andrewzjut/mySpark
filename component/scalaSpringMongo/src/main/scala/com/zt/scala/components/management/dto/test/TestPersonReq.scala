package com.zt.scala.components.management.dto.test

import io.swagger.annotations.{ApiModel, ApiModelProperty}
import org.hibernate.validator.constraints.NotEmpty

import scala.beans.BeanProperty

@ApiModel("TestPersonReq")
@SerialVersionUID(-6503532830143322549L)
class TestPersonReq extends Serializable {
  @NotEmpty(message = "唯一识别码不能为空")
  @BeanProperty
  @ApiModelProperty(value = "用户识别码", name = "code", dataType = "string")
  var code: String = _
  @NotEmpty(message = "用户名不能为空")
  @BeanProperty
  @ApiModelProperty(value = "用户名称", name = "name", dataType = "string")
  var name: String = _
  @NotEmpty(message = "用户年龄不能为空")
  @BeanProperty
  @ApiModelProperty(value = "用户年龄", name = "age", dataType = "integer")
  var age: Int = _
  @BeanProperty
  @NotEmpty(message = "用户地址不能为空")
  @ApiModelProperty(value = "用户地址", name = "address", dataType = "string")
  var address: String = _
  @NotEmpty(message = "唯一性别不能为空")
  @BeanProperty
  @ApiModelProperty(value = "用户性别", name = "gender", dataType = "boolean")
  var gender: Boolean = _
}