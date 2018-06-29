package com.zt.scala.components.management.service

import com.mongodb.WriteResult
import com.typesafe.scalalogging.LazyLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query, Update}

class BasicService @Autowired()(implicit mongoTemplate: MongoTemplate) extends LazyLogging {
  protected def basicUpdate(id: String, update: Update, clz: Class[_]): WriteResult = {
    val query = new Query();
    query.addCriteria(new Criteria("_id").is(id))
    mongoTemplate.updateFirst(query, update, clz)
  }
}
