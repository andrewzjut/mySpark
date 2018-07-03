/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package com.zt.spark.rdd.classificationAndRegression

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

// $example on$
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.util.MLUtils

import scala.collection.mutable.Set

// $example off$

object NaiveBayesExample {

  def loadDataSet(sc: SparkContext, filename: String): Set[String] = {
    val set = Set[String]()
    val data = sc.textFile(filename).flatMap(word => word.split("\\s+")).filter(_.length > 2)
    data.collect().foreach(set.add(_))
    set
  }

  def createVocalList(set: Set[Set[String]]): List[String] = {
    var vocabSet = Set[String]()
    for (s <- set) {
      vocabSet = vocabSet | s
    }
    vocabSet.toList
  }

  def setOfWord2Vec(vocabList: List[String], inputSet: Set[String]): Array[Int] = {
    var arr = Array.fill(vocabList.size)(0)
    for (word <- inputSet) {
      if (vocabList.contains(word)) {
        arr = arr.updated(vocabList.indexOf(word), 1)
      }
    }
    arr
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("NaiveBayesExample").setMaster("local")
    val sc = new SparkContext(conf)
    // $example on$
    // Load and parse the data file.

    val set1 = loadDataSet(sc, "D:\\myPlace\\Spark2.0Learnning\\src\\main\\resources\\1.txt")
    val set2 = loadDataSet(sc, "D:\\myPlace\\Spark2.0Learnning\\src\\main\\resources\\2.txt")
    val vocabList = createVocalList(Set(set1, set2))
    val set1Vector = setOfWord2Vec(vocabList, set1)




    val data: RDD[LabeledPoint] = MLUtils.loadLibSVMFile(sc, "D:\\myPlace\\Spark2.0Learnning\\src\\main\\resources\\regression\\sample_libsvm_data.txt")
    // Split data into training (60%) and test (40%).
    //    data.collect().foreach(println(_))
    val Array(training, test) = data.randomSplit(Array(0.6, 0.4))

    val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")

    val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()

    println(accuracy)
    // Save and load model
    /*model.save(sc, "target/tmp/myNaiveBayesModel")
    val sameModel = NaiveBayesModel.load(sc, "target/tmp/myNaiveBayesModel")*/
    // $example off$

    sc.stop()
  }
}

// scalastyle:on println
