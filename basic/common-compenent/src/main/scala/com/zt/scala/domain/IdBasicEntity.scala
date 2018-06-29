package com.zt.scala.domain

import scala.beans.BeanProperty

@SerialVersionUID(2717918492662526949L)
class IdBasicEntity extends Serializable {

  @BeanProperty
  var id: String = _
}