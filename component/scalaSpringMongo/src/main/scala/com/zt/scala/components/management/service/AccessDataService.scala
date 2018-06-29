package com.zt.scala.components.management.service


import com.ecfront.dew.common.{Page, Resp}
import com.zt.scala.common.repository.AccessDataRepository
import com.zt.scala.components.management.dto.{AccessDataReq, AccessDataResp}
import com.zt.scala.domain.AccessDataEntity
import com.zt.scala.page.PageRequest
import com.zt.scala.utils.{DateHelper, JsonHelper}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import java.util

import com.fasterxml.jackson.databind.node.ArrayNode

import scala.collection.JavaConversions._
import scala.collection.mutable

@Service
class AccessDataService @Autowired()(val accessDataRepository: AccessDataRepository
                                     , implicit val mongoTemplate: MongoTemplate) extends BasicService {
  def findByCode(code: String, pageNum: Int, pageSize: Int): Resp[Page[AccessDataResp]] = {
    Resp.success(accessDataRepository.findByCodeLikeAndDelFlag(code, delFlag = false, new PageRequest(pageNum, pageSize)))
  }

  def findAll(pageNum: Int, pageSize: Int): Resp[Page[AccessDataResp]] = {
    Resp.success(accessDataRepository.findByDelFlag(delFlag = false, new PageRequest(pageNum, pageSize)))
  }


  def addAccessData(accessDataReq: AccessDataReq): Resp[Void] = {
    val accessDataOld = getAccessDataByCode(accessDataReq.getCode)
    if (accessDataOld != null) {
      return Resp.badRequest("当前记录已存在")
    }
    val accessData = new AccessDataEntity
    accessData.setCode(accessDataReq.getCode)
    accessData.setBatchSize(accessDataReq.getBatchSize)
    accessData.setDesc(accessDataReq.getDesc)
    accessData.setTables(transformTable(accessDataReq.getTables))
    accessData.setFinger(getFinger(accessData.getTables))
    accessData.setVersion(1)
    accessData.setName(accessDataReq.getName)
    accessData.setSaveToODS(accessDataReq.getSaveToODS)
    accessData.setSaveToRDS(accessDataReq.getSaveToRDS)
    val currentTime = DateHelper.getCurrentDateWithTimestamp()
    accessData.setCreateTime(currentTime)
    accessData.setUpdateTime(currentTime)
    println(JsonHelper.toJsonString(accessData))
    accessDataRepository.insert(accessData)
    Resp.success(null)
  }


  def getAccessDataByCode(code: String): AccessDataEntity = {
    accessDataRepository.getOneByCodeAndDelFlag(code, delFlag = false)
  }

  private def getFinger(tables: util.List[AccessDataEntity.Table]): util.HashMap[String, String] = {
    val objNode = new util.HashMap[String, String]()
    for (table: AccessDataEntity.Table <- tables) {
      objNode.put(table.getName, table.getMapping.values().toList.sortBy(_.hashCode).mkString(","))
    }
    objNode
  }

  private def transformTable(accessDataTables: mutable.Buffer[AccessDataReq.Table]): util.List[AccessDataEntity.Table] = {
    val tables: mutable.Buffer[AccessDataEntity.Table] = new mutable.ArrayBuffer[AccessDataEntity.Table]()
    if (accessDataTables != null) {
      for (tb: AccessDataReq.Table <- accessDataTables) {
        val table = new AccessDataEntity.Table
        table.name = tb.name
        table.mapping = transformTableColumn(tb.mapping)
        tables.add(table)
      }
    }
    tables
  }

  private def transformTableColumn(columnNames: ArrayNode): mutable.Map[String, String] = {
    val tableColumn: mutable.Map[String, String] = new mutable.HashMap[String, String]
    if (columnNames.size() > 0) {
      for (columnName <- columnNames) {
        val key = columnName.get("key").asText()
        if (!key.isEmpty) {
          tableColumn.+=(key -> columnName.get("value").asText())
        }
      }
    }
    tableColumn
  }
}
