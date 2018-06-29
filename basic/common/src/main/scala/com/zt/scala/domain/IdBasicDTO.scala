package com.zt.scala.domain

import scala.beans.BeanProperty

class IdBasicDTO extends Serializable {
  @BeanProperty
  var id: String = _ //变量初始化可以用用 _ 作占位符，赋值为默认值，字符串 null，Float、Int、Double 等为 0
}