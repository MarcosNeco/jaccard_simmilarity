package com.jacsimm.core

import com.jacsimm.configuration.Configuration
import com.jacsimm.model.DocumentView
import com.jacsimm.store.{DocumentsRelationshipStore, Relationship, RelationshipKey}
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
     val ssc = new StreamingContext(sparkConf, Seconds(5))

     val message = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Configuration.topicSet, Configuration.kafkaParams))

     val spark = SparkSession.builder.config(ssc.sparkContext.getConf).getOrCreate()
     import spark.implicits._

     message
       .map(record => DocumentView(record.key(), record.value()))
       .foreachRDD(rdd=>{
         val documentsViewDF = rdd.toDF()

         val documentsViewAndTotal = documentsViewDF
           .withColumn("totalDocuments", count("document").over(countTotalByDocument))

         //self join to combine all relation users
         val jaccardCalculatedDf = documentsViewAndTotal.as("docViewLeft")
           .join(documentsViewAndTotal.as("docViewRight"), col("docViewLeft.user") === col("docViewRight.user"))
           .filter(col("docViewLeft.document") =!= col("docViewRight.document"))
           .groupBy(col("docViewLeft.document").as("docA"), col("docViewRight.document").as("docB"))
           .agg(max("docViewLeft.totalDocuments").as("totalDocA"),
                max("docViewRight.totalDocuments").as("totalDocB"),
                count("*").as("totalInCommon"))
           .withColumn("jaccardIndex", jaccardIndex(col("totalInCommon"), col("totalDocA"), col("totalDocB")))

        val jaccardCalculatedAsObj = jaccardCalculatedDf.map(row=>{
           Relationship(RelationshipKey(row.getAs[String]("docA"), row.getAs[String]("docB")),
             row.getAs[Float]("jaccardIndex"))
         }).collect()

         DocumentsRelationshipStore.addAllAndRecalculateIndex(jaccardCalculatedAsObj)
     })

     ssc.start()
     ssc.awaitTermination()

  }

  private val countTotalByDocument = {
    Window.partitionBy("document").orderBy("document")
  }

  val jaccardIndex = udf((userInCommon: Int, totalDocA: Int, totalDocB: Int) =>  {
    val union = totalDocA + totalDocB - userInCommon
    (userInCommon / union).toFloat
  })

}


