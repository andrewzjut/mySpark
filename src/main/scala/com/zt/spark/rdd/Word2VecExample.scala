package com.zt.spark.rdd

import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by hzzt on 2017/3/15.
  */
object Word2VecExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("recommendation")
    val sc = new SparkContext(conf)
    val input =sc.textFile("/Users/zhangtong/IdeaProjects/mySpark/src/main/resources/2city11.txt")
      .map(line=>line.split(" ").toSeq)
    val word2vec = new Word2Vec()
    val model = word2vec.fit(input)

    val synonyms = model.findSynonyms("keeper",40)
    for((synonym,cosineSimilarity) <- synonyms){
      println(s"$synonym $cosineSimilarity")
    }
  }
}
