package com.zt.scala.kafka

import java.util
import java.util.Properties
import java.util.concurrent.ExecutionException

import org.apache.kafka.clients.admin._
import com.zt.scala.constant.KafkaProperties
import org.apache.kafka.common.config.ConfigResource

import scala.collection.JavaConverters._

object MyAdminClient {
  def main(args: Array[String]): Unit = {
    val topic = "dmp.gateway.source-dop.fdn.t_app_user";
    val props = new Properties()
    props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProperties.BOOTSTRAPS)
    val client: AdminClient = AdminClient.create(props)
    describeCluster(client)
    println()
    describeConfig(client, topic)

    describeTopics(client)
  }


  def describeCluster(client: AdminClient): Unit = {
    val ret: DescribeClusterResult = client.describeCluster()
    println(String.format("Cluster id: %s, controller: %s", ret.clusterId().get(), ret.controller().get()))
    println("Current cluster nodes info: ")

    try {
      ret.nodes().get.asScala.foreach(node => println(node))
    } catch {
      case e: InterruptedException => throw e
      case e: ExecutionException => throw e
    }
  }

  def describeConfig(client: AdminClient, topic: String): Unit = {
    val ret: DescribeConfigsResult = client.describeConfigs(util.Collections.singletonList(new ConfigResource(ConfigResource.Type.TOPIC, topic)))
    val config = ret.all().get()
    for ((key, value) <- config.asScala) {
      println(String.format("Resource type: %s, resource name: %s", key.`type`, key.name))

      val configEntries = value.entries
      configEntries.asScala.foreach(entry =>
        println(entry.name + " = " + entry.value)
      )
    }
  }

  def alterConfig(adminClient: AdminClient, topic: String): Unit = {
    val topicConfig = new Config(util.Arrays.asList(new ConfigEntry("cleanup.policy", "compact")))
    adminClient.alterConfigs(util.Collections.singletonMap(new ConfigResource(ConfigResource.Type.TOPIC, topic), topicConfig)).all().get()
  }

  def deleteTopics(adminClient: AdminClient, topics: List[String]): Unit = {
    adminClient.deleteTopics(topics.asJava)
  }

  def describeTopics(adminClient: AdminClient): Unit = {
    val ret: DescribeTopicsResult = adminClient.describeTopics(util.Arrays.asList("__consumer_offsets"))
    val topics = ret.all().get().asScala
    for ((topic, topicDescription) <- topics)
      println(s"$topic ===>  $topicDescription")
  }

  def createTopics(adminClient: AdminClient, topic: String): Unit = {
    val newTopic: NewTopic = new NewTopic(topic, 3, 3)
    val ret: CreateTopicsResult = adminClient.createTopics(util.Arrays.asList(newTopic))
    ret.all().get()
  }

  def listAllTopics(adminClient: AdminClient): Unit = {
    val options = new ListTopicsOptions()
    options.listInternal(true)
    val topics: ListTopicsResult = adminClient.listTopics(options)
    val topicNames = topics.names().get()
    println(s"Current topics in this cluster: $topicNames")
  }
}
