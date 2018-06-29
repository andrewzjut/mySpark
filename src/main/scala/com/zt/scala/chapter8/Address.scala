//package com.zt.scala.chapter8
//
//case class Address(street: String, city: String, zip: String) {
//  def this(zip: String) =
//    this("[unknown]", Address.zipToCity(zip), Address.zipToState(zip), zip)
//}
//
////class Address 的伴生对象 内部是 静态方法  静态敞亮
//object Address {
//  val COUNTRY = "USA"
//
//  def zipToCity(zip: String) = "Anytown"
//
//  def zipToState(zip: String) = "CA"
//}
//
//case class Person(name: String, age: Option[Int], address: Option[Address]) {
//  def this(name: String) = this(name, None, None)
//
//  def this(name: String, age: Int) = this(name, Some(age), None)
//
//  def this(name: String, age: Int, address: Address) = this(name, Some(age), Some(address))
//
//  def this(name: String, address: Address) = this(name, None, Some(address))
//}
//
//class Employee(
//                name: String,
//                age: Option[Int] = None,
//                address: Option[Address] = None,
//                val title: String = "[unknown]",
//                val manager: Option[Employee] = None) extends Person(name, age, address) {
//  override def toString = s"Employee($name, $age, $address, $title, $manager)"
//}