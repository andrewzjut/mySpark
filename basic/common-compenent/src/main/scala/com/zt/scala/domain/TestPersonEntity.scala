package com.zt.scala.domain

import org.springframework.data.mongodb.core.mapping.Document

import scala.beans.BeanProperty

@Document(collection = "testPerson")
@SerialVersionUID(8691642732503871050L)
class TestPersonEntity extends SecurityBasicEntity {

  @BeanProperty
  var code: String = _
  @BeanProperty
  var name: String = _
  @BeanProperty
  var age: Int = _
  @BeanProperty
  var address: String = _
  @BeanProperty
  var gender: Boolean = _
}
