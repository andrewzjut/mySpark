package com.zt.scala

import com.alibaba.fastjson.serializer.SerializerFeature
import com.alibaba.fastjson.{JSON, JSONObject}
import com.zt.scala.`implicit`.Person

import scala.beans.BeanProperty


object Test1 extends App {

  val str2 = "{\"et\":\"kanqiu_client_join\",\"vtm\":1435898329434,\"body\":{\"client\":\"866963024862254\",\"client_type\":\"android\",\"room\":\"NBA_HOME\",\"gid\":\"\",\"type\":\"\",\"roomid\":\"\"},\"time\":1435898329}"
  var json: JSONObject = JSON.parseObject(str2);
  println(json)

  val person1 = Person2("zhang", 12)
  val person2 = Person2("zhang", 12)


  val jsonString = JSON.toJSONString(person1, SerializerFeature.WriteMapNullValue)
  println(jsonString)

  val person3 = JSON.parseObject(jsonString,classOf[Person])
}

case class Person2(@BeanProperty val name: String, @BeanProperty var age: Int) extends Serializable{

}