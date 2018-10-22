package com.jacsimm.core

import com.jacsimm.configuration.Configuration
import com.jacsimm.model.DocumentView
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object JaccardSimilarityAlgorithm{

  def main(args: Array[String]): Unit = {
    calculate()
  }

  def calculate(): Unit ={
     val sparkConf = new SparkConf().setMaster("local[*]").setAppName("streaming-kafkaviewdoc")
     val ssc = new StreamingContext(sparkConf, Seconds(Configuration.intervalReadStream))

     val message = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Configuration.topicSet, Configuration.kafkaConsumerParams))

     val spark = SparkSession.builder.config(ssc.sparkContext.getConf).getOrCreate()
     import spark.implicits._

     message
       .map(record => {
         val msg  = record.value().split(Configuration.messageSeparator)
         DocumentView(msg(0).toLong, msg(1))
       }).foreachRDD(rdd=>{
         val documentsViewDF = rdd.toDF()
         val documentsViewAndTotal = documentsViewDF
           .withColumn("totalDocuments", count("document").over(countTotalByDocument))

         //self join to combine all relation users
         val jaccardCalculatedDf = documentsViewAndTotal.as("docViewLeft")
           .join(documentsViewAndTotal.as("docViewRight"), col("docViewLeft.user") === col("docViewRight.user"))
           .filter(col("docViewLeft.document") < col("docViewRight.document"))
           .groupBy(col("docViewLeft.document").as("docA"), col("docViewRight.document").as("docB"))
           .agg(max("docViewLeft.totalDocuments").as("totalDocA"),
                max("docViewRight.totalDocuments").as("totalDocB"),
                count("*").as("totalInCommon"))
           .withColumn("jaccardIndex", jaccardIndex(col("totalInCommon"), col("totalDocA"), col("totalDocB")))
           .select("docA", "docB", "jaccardIndex")

         if(existHistoricData(spark)) {
          val previousProcessingDF = spark.sql(s"select * from ${Configuration.jaccardSimilarityTmpTable}")
          val recalculatedJaccardIndex = jaccardCalculatedDf
            .union(previousProcessingDF)
            .groupBy("docA", "docB")
            .agg(avg("jaccardIndex").as("jaccardIndex"))
          recalculatedJaccardIndex.createOrReplaceGlobalTempView(Configuration.jaccardSimilarityTmpTable)
        }else{
          jaccardCalculatedDf.createOrReplaceGlobalTempView(Configuration.jaccardSimilarityTmpTable)
        }
     })

     ssc.start()
     ssc.awaitTermination()

  }

  private def existHistoricData(spark: SparkSession): Boolean ={
    spark.sqlContext.tableNames().contains(Configuration.jaccardSimilarityTmpTable)
  }
  private val countTotalByDocument = {
    Window.partitionBy("document").orderBy("document")
  }

  val jaccardIndex = udf((userInCommon: Int, totalDocA: Int, totalDocB: Int) =>  {
    val union = totalDocA + totalDocB - userInCommon
    (userInCommon / union).toFloat
  })

}


