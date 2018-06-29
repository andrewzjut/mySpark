package com.zt.scala.common.repository


import com.zt.scala.domain.AccessDataEntity
import org.springframework.data.domain.{Page, Pageable}
import org.springframework.data.mongodb.repository.MongoRepository

trait AccessDataRepository extends MongoRepository[AccessDataEntity, String] {
  def findByCodeLikeAndDelFlag(code: String, delFlag: Boolean, pageable: Pageable): Page[AccessDataEntity]

  def findByDelFlag(delFlag: Boolean, pageable: Pageable): Page[AccessDataEntity]

  def getOneByCodeAndDelFlag(code: String, delFlag: Boolean): AccessDataEntity
}
