package com.jacsimm.core

import com.jacsimm.configuration.Configuration
import com.jacsimm.model.DocumentView
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object JaccardSimilarityAlgorithm{

  def main(args: Array[String]): Unit = {
    calculate()
  }

  def calculate(): Unit ={
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("streaming-kafkaviewdoc")
    val ssc = new StreamingContext(sparkConf, Seconds(10))

     val message = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Configuration.topicSet, Configuration.kafkaParams))

     val documentsView  = message
       .map(record => (record.key(), record.value()))

     val totalByDocument = documentsView
       .groupByKey()
       .map((document) => (document._1, document._2.size))


     ssc.start()
     ssc.awaitTermination()

  }

  def generateCoefficient(): Unit ={

  }
}


