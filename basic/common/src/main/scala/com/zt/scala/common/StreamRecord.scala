package com.zt.scala.common

import java.sql.Timestamp

case class StreamRecord[E](topic: String, timestamp: Timestamp, body: E) extends Serializable

case class Visit(referer: String, url: String, userFlag: String, ip: String, time: Long) extends Serializable

case class Person(name:String,age:Int)

case class DeviceData(device: String, deviceType: String, signal: Double, time: Long)

case class Animal(timestamp: Timestamp,name:String)