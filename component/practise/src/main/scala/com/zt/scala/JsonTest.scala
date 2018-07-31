package com.zt.scala

import com.alibaba.fastjson.serializer.SerializerFeature
import com.alibaba.fastjson.{JSON, JSONObject}
import com.google.gson.{Gson, JsonObject, JsonParser}
import com.zt.scala.chapter4.{Address, Person}

class JsonTest {

}

class A extends Serializable {
  val age: Map[String, Int] = Map("age" -> 26)
  val address: Map[String, Map[String, String]] = Map("city" -> Map("hangzhou" -> "binjiang"))
  val name = "zhang"
}

object JsonTest extends App {
  val json: String = JSON.toJSONString(new A(), SerializerFeature.WRITE_MAP_NULL_FEATURES)
  println(json)


  val jsonString = "{\"host\":\"td_test\",\"ts\":1486979192345,\"device\":{\"tid\":\"a123456\",\"os\":\"android\",\"sdk\":\"1.0.3\"},\"time\":1501469230058}"
  val jsonParser = new JsonParser()
  val obj = jsonParser.parse(jsonString).asInstanceOf[JsonObject]
  println(obj.get("device"))


  val p = Person("Alvin Alexander", 16, Address("binhelu", "hangzhou", "china"))
  val gson = new Gson
  val personString = gson.toJson(p)
  println(personString)

  val aString = gson.toJson(new A)
  println(aString)

}