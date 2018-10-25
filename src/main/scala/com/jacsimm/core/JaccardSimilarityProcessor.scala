package com.jacsimm.core

import com.jacsimm.configuration.Configuration
import com.jacsimm.model.DocumentView
import com.jacsimm.session.SparkBuilderSession
import com.jacsimm.store.DocumentsRelationshipStore
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.FloatType
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object JaccardSimilarityProcessor{

  val spark = new SparkBuilderSession().build()
  import spark.implicits._

  def launcher(): Unit ={
     println("launcher stream application processor...")
     DocumentsRelationshipStore.createEmptyHistoricalData()
     val ssc = new StreamingContext(spark.sparkContext, Seconds(Configuration.intervalReadStream))

     val message = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Configuration.topicSet, Configuration.kafkaConsumerParams))

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

          val previousProcessingDF = DocumentsRelationshipStore.getAllHistoricalData()
          val recalculatedJaccardIndex = jaccardCalculatedDf
            .union(previousProcessingDF)
            .groupBy("docA", "docB")
            .agg(avg("jaccardIndex").cast(FloatType).as("jaccardIndex"))
          DocumentsRelationshipStore.storeOrUpdate(recalculatedJaccardIndex)
     })

     ssc.start()
     ssc.awaitTermination()

  }


  private val countTotalByDocument = {
    Window.partitionBy("document").orderBy("document")
  }

  val jaccardIndex = udf((userInCommon: Int, totalDocA: Int, totalDocB: Int) =>  {
    val union = totalDocA + totalDocB - userInCommon
    (userInCommon.toFloat / union.toFloat)
  })

}


