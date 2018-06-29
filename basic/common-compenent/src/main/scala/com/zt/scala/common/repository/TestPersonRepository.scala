package com.zt.scala.common.repository

import com.zt.scala.domain.TestPersonEntity
import org.springframework.data.domain.{Page, Pageable}
import org.springframework.data.mongodb.repository.MongoRepository


trait TestPersonRepository extends MongoRepository[TestPersonEntity, String] {

  def findByDelFlag(delFlag: Boolean, pageable: Pageable): Page[TestPersonEntity]

  def getOneByCodeAndDelFlag(code: String, delFlag: Boolean): TestPersonEntity

  def deleteTestPersonEntityByCode(code: String): Long;

  def findByCode(code: String): TestPersonEntity

  def findByName(name: String, pageable: Pageable): Page[TestPersonEntity]

  def findByAge(age: Int, pageable: Pageable): Page[TestPersonEntity]

  def findByAddress(address: String): TestPersonEntity

  def findByAgeGreaterThan(age: Int, pageable: Pageable): Page[TestPersonEntity]

  def findByNameNotNull(pageable: Pageable): Page[TestPersonEntity]

  def findByNameLike(name: String, pageable: Pageable): Page[TestPersonEntity]

  def deleteByName(name: String): List[TestPersonEntity]

  def deleteTestPersonEntityByName(name: String): Long
}
