package com.jacsimm.core

import com.jacsimm.configuration.Configuration
import com.jacsimm.model.DocumentView
import org.apache.spark.sql.{Column, SparkSession}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object JaccardSimilarityAlgorithm{

  def main(args: Array[String]): Unit = {
    calculate()
  }

  def calculate(): Unit ={
     val sparkConf = new SparkConf().setMaster("local[*]").setAppName("streaming-kafkaviewdoc")
     val ssc = new StreamingContext(sparkConf, Seconds(60))

     val message = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Configuration.topicSet, Configuration.kafkaParams))

     val tempTables = List[String]
     message
       .map(record => DocumentView(record.key(), record.value()))
       .foreachRDD(rdd=>{
         val spark = SparkSession.builder.config(rdd.sparkContext.getConf).getOrCreate()
         import spark.implicits._
         val documentsViewDF = rdd.toDF()

         val documentsViewAndTotal = documentsViewDF
           .withColumn("totalDocuments", count("document").over(countTotalByDocument()))

         //self join to combine all relation users
         val calculatedDf = documentsViewAndTotal.as("docViewLeft")
           .join(documentsViewAndTotal.as("docViewRight"), $"docViewLeft.user" === $"docViewRight.user")
           .filter($"docViewLeft.document" =!= $"docViewRight.document")
           .groupBy(col("docViewLeft.document").as("docA"), col("docViewRight.document").as("docB"))
           .agg(max("docViewLeft.totalDocuments").as("totalDocA"),
                max("docViewRight.totalDocuments").as("totalDocB"),
                count("*").as("totalInCommon"))
           .withColumn("jaccardIndex", jaccardIndex($"totalInCommon", $"totalDocA", $"totalDocB"))

         val tempTableToRdd = "jaccard_table" + rdd.id.toString
         tempTables +: tempTableToRdd
         calculatedDf.createOrReplaceTempView(tempTableToRdd)
      })


     ssc.start()
     ssc.awaitTermination()

  }

  private val countTotalByDocument = {
    Window.partitionBy("document").orderBy("document")
  }

  val jaccardIndex = udf(jaccardIndexFunc)

  private val jaccardIndexFunc = (userInCommon: Int, totalDocA: Int, totalDocB: Int) => Float {
      val union = totalDocA + totalDocB - userInCommon
      userInCommon / union
  }

}


