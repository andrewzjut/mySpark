package com.zt.scala.domain

import scala.beans.BeanProperty

class SecurityBasicDTO extends IdBasicDTO {
  @BeanProperty
  var createUser: String = _
  @BeanProperty
  var createTime: Long = 0L
  @BeanProperty
  var updateUser: String = _
  @BeanProperty
  var updateTime: Long = 0L
  @BeanProperty
  var delFlag: Boolean = false
  @BeanProperty
  var enabled: Boolean = true

}
