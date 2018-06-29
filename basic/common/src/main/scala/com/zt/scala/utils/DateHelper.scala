package com.zt.scala.utils

import java.util.Date

/**
  * Created on 2018/4/26.
  *
  * @author 迹_Jason
  */
object DateHelper {

  def getCurrentDateWithTimestamp(): Long = {
    new Date().getTime
  }
}
