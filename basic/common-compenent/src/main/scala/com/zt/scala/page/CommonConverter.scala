package com.zt.scala.page

import java.util

import com.ecfront.dew.common.{$, Page}

import scala.collection.JavaConverters._
import scala.reflect.ClassTag

object CommonConverter {
//todo 原始 TypeTag  突然失效
  def convert[E: ClassTag](ori: AnyRef): E = {
    val dest = implicitly[ClassTag[E]].runtimeClass.newInstance.asInstanceOf[E]
    $.bean.copyProperties(dest, ori)
    dest
  }

  def convertPage[E: ClassTag, O <: AnyRef](ori: org.springframework.data.domain.Page[O]): Page[E] = {
    if (ori.getContent.size() > 0) {
      import scala.collection.JavaConversions._
      Page.build(ori.getNumber(), ori.getSize(), ori.getTotalElements, ori.getContent.map(convert[E](_)))
    } else {
      val dto = new Page[E]
      dto.setPageNumber(ori.getNumber())
      dto.setPageSize(ori.getSize())
      dto.setRecordTotal(ori.getTotalElements)
      dto.setPageTotal(ori.getTotalPages)
      dto.setObjects(null)
      dto
    }
  }

  def convertList[E: Manifest, O <: AnyRef](ori: util.List[O]): util.List[E] = {
    if (ori.size() > 0) {
      ori.asScala.map(convert[E](_)).asJava
    } else {
      new util.ArrayList[E]()
    }
  }
}
