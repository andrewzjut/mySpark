package com.zt.spark.rdd.classificationAndRegression

import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by hzzt on 2017/3/7.
  */

object NaiveBayesEmail {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("EmailTest")
    val sc = new SparkContext(conf)
    val inputRdd = sc.textFile("D:\\myPlace\\Spark2.0Learnning\\src\\main\\resources\\SMSSpamCollection")
    val spamRDD1 = inputRdd.filter(x => x.split("\t")(0) == "spam")
    val spam = spamRDD1.map(x => x.split("\t")(1))

    val hamRDD1 = inputRdd.filter(x => x.split("\t")(0) == "ham")
    val ham = hamRDD1.map(x => x.split("\t")(1))

    import org.apache.spark.mllib.feature.HashingTF
    val tf = new HashingTF(200)
    val spamFeatures = spam.map(msg => tf.transform(msg.split(" ").filter(_.length > 2)))
    val hamFeatures = ham.map(msg => tf.transform(msg.split(" ").filter(_.length > 2)))
    spamFeatures.collect().foreach(println(_))
    //  Create LabeledPoint datasets for positive (spam) and negative (ham) examples.
    val positiveExamples = spamFeatures.map(featrues => LabeledPoint(1, featrues))
    val negativeExamples = hamFeatures.map(features => LabeledPoint(0, features))
    val training_data = positiveExamples.union(negativeExamples)
    training_data.cache()

    val Array(training, test) = training_data.randomSplit(Array(0.6, 0.4))
    val model = NaiveBayes.train(training, 1.0)
    val predictionLabel = test.map(x => (model.predict(x.features), x.label))
    val accuracy = 1.0 * predictionLabel.filter(x => x._1 == x._2).count() / test.count()
    println(accuracy)
  }
}
