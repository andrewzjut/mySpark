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

import org.apache.spark.{SparkConf, SparkContext}

// $example on$
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}

// $example off$

@deprecated("Use ml.regression.LinearRegression or LBFGS", "2.0.0")
object LinearRegressionWithSGDExample {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("LinearRegressionWithSGDExample").setMaster("local[2]")
    val sc = new SparkContext(conf)
    sc.addJar("D:\\myPlace\\Spark2.0Learnning\\out\\artifacts\\Spark2_0Learnning_jar\\Spark2.0Learnning.jar")

    // $example on$
    // Load and parse the data
    val data = sc.textFile("D:\\myPlace\\Spark2.0Learnning\\src\\main\\resources\\ex0.txt")

    //800	0	0.3048	71.3	0.00266337	126.201
    val parsedData = data.map { line =>
      val parts = line.split("\\s+").map(_.toDouble)
      LabeledPoint(parts(2), Vectors.dense(parts.init))
    }.cache()

    // Building the model
    val numIterations = 10000
    val stepSize = 0.01
    val model = LinearRegressionWithSGD.train(parsedData, numIterations, stepSize)

    // Evaluate model on training examples and compute training error
    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    valuesAndPreds.collect().foreach(println(_))
    /*val test = "-1.63735562648104 -2.00621178480549 -1.86242597251066 -1.02470580167082 -0.522940888712441 -0.863171185425945 -1.04215728919298 -0.864466507337306"
    val point = LabeledPoint(-0.4307829,Vectors.dense(test.split(' ').map(_.toDouble)))

    val  pointPrediction = model.predict(point.features)
    println("=====")
    println(pointPrediction)*/
    val MSE = valuesAndPreds.map { case (v, p) => math.pow((v - p), 2) }.mean()

    println("training Mean Squared Error = " + MSE)

    // Save and load model
    //model.save(sc, "hdfs://172.30.251.30:9000/user/hzzt/spark/out/tmp/scalaLinearRegressionWithSGDModel")
    //val sameModel = LinearRegressionModel.load(sc, "hdfs://172.30.251.30:9000/user/hzzt/spark/out/tmp/scalaLinearRegressionWithSGDModel")
    // $example off$

    sc.stop()
  }
}

// scalastyle:on println
