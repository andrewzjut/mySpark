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
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils

// $example off$

object SVMWithSGDExample {
  def sign(a: Double): Int = if (a > 0) 1 else 0

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SVMWithSGDExample").setMaster("local[2]")
    val sc = new SparkContext(conf)

    // $example on$
    // Load training data in LIBSVM format.
     val data: RDD[LabeledPoint] = MLUtils.loadLibSVMFile(sc, "D:\\myPlace\\Spark2.0Learnning\\src\\main\\resources\\regression\\sample_libsvm_data.txt")
//    val data = sc.textFile("D:\\myPlace\\Spark2.0Learnning\\src\\main\\resources\\regression\\testSetRBF2.txt")
//    val parsedData = data.map { line =>
//      val parts = line.split("\\s+").map(_.toDouble)
//      if (parts(2) > 0)
//        parts(2) = 1
//      else
//        parts(2) = 0
//      LabeledPoint(parts(2), Vectors.dense(parts.init))
//    }.cache()

    // Split data into training (60%) and test (40%).
    val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    // Run training algorithm to build the model
    val numIterations = 100

    import org.apache.spark.mllib.optimization.L1Updater

    val svmAlg = new SVMWithSGD()
    svmAlg.optimizer
      .setNumIterations(100)
      .setRegParam(0.01)
      .setUpdater(new L1Updater)

    val model = svmAlg.run(training)

    // Clear the default threshold.
    model.clearThreshold()
    // Compute raw scores on the test set.
    val scoreAndLabels = test.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }
    val testNum = test.count()
    val result = scoreAndLabels.map(x => (sign(x._1), x._2))
    val errNum = result.filter(x => x._1 != x._2).count()
    println(errNum)
    println(testNum)
    println(errNum * 1.0 / testNum)
    // Get evaluation metrics.
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)

    val auROC = metrics.areaUnderROC()
    println("Area under ROC = " + auROC)

    // Save and load model
    //    model.save(sc, "target/tmp/scalaSVMWithSGDModel")
    //    val sameModel = SVMModel.load(sc, "target/tmp/scalaSVMWithSGDModel")
    // $example off$

    sc.stop()
  }
}

// scalastyle:on println
