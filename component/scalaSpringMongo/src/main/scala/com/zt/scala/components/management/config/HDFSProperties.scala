package com.zt.scala.components.management.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

import scala.beans.BeanProperty


@Component
@ConfigurationProperties(prefix = "scala.hdfs")
class HDFSProperties {

  @BeanProperty
  var originPath: String = _
  @BeanProperty
  var destinationPath: String = _
  @BeanProperty
  var hdfsUri: String = _
}
