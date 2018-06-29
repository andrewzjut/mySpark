package com.zt.scala

import com.alibaba.fastjson.serializer.SerializerFeature
import com.alibaba.fastjson.{JSON, JSONObject}

object FastjsonHelper extends Serializable {
  /**
    * object 转 json字符串
    *
    * @param obj object
    * @return json字符串
    */
  def toJsonString(obj: Any): String = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue)

  /**
    * object 转 json
    *
    * @param obj object
    * @return json
    */
  def toJson(obj: Any): JSONObject = {
    obj match {
      case o: String => JSON.parseObject(o)
      case _ => JSON.parseObject(toJsonString(obj))
    }
  }

  /**
    * json或string 转 object
    *
    * @param obj json或string
    * @return object
    */
  def toObject[E](obj: Any, clazz: Class[E]): E = {
    try {
      obj match {
        case o: String =>
          clazz match {
            case c if c == classOf[String] =>
              o.asInstanceOf[E]
            case c if c == classOf[Int] =>
              o.toInt.asInstanceOf[E]
            case c if c == classOf[Long] =>
              o.toLong.asInstanceOf[E]
            case c if c == classOf[Double] =>
              o.toDouble.asInstanceOf[E]
            case c if c == classOf[Float] =>
              o.toFloat.asInstanceOf[E]
            case c if c == classOf[Boolean] =>
              o.toBoolean.asInstanceOf[E]
            case c if c == classOf[Byte] =>
              o.toByte.asInstanceOf[E]
            case c if c == classOf[Short] =>
              o.toShort.asInstanceOf[E]
            case c if c == classOf[Void] =>
              null.asInstanceOf[E]
            case _ =>
              JSON.parseObject(o, clazz)
          }
        case o: JSONObject => JSON.parseObject(o.toString, clazz)
        case _ => JSON.parseObject(toJsonString(obj), clazz)
      }
    } catch {
      case e: Throwable =>
        throw e
    }
  }

  def main(args: Array[String]): Unit = {
    val person2 = new Person2("zhang", 12)
    val jsonString = toJsonString(person2)
    println(jsonString)
    val jsonObject = toJson(jsonString)
    println(jsonObject.getString("name"))
    val person3 = toObject[Person2](person2, classOf[Person2])
    println(person3.getName)
    val person4 = toObject[Person2](jsonString, classOf[Person2])
    println(person4.getAge)
    println(toObject[Long]("5555555", classOf[Long]))
    println("555555".toLong)
  }
}
