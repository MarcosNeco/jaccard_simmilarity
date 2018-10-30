package com.jacsimm.consumer

import com.jacsimm.configuration.Configuration
import com.jacsimm.core.JaccardSimilarityProcessor
import com.jacsimm.model.DocumentView
import com.jacsimm.session.SparkBuilderSession
import com.jacsimm.store.DocumentsRelationshipStore
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

object ConsumerDocView {

  val spark = new SparkBuilderSession().build()
  private lazy val storageDocuments = DocumentsRelationshipStore.getInstance()

  def startConsume={
    println("launcher stream application processor...")
    storageDocuments.createEmptyHistoricalData()
    val ssc = new StreamingContext(spark.sparkContext, Seconds(Configuration.intervalReadStream))

    val message = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Configuration.topicSet, Configuration.kafkaConsumerParams))

    val jaccardProcessor = new JaccardSimilarityProcessor(storageDocuments)
    message
      .map(record => {
        val msg  = record.value().split(Configuration.messageSeparator)
        DocumentView(msg(0).toLong, msg(1))
     }).foreachRDD(rdd=> {
          val documentsViewDF = convertToDataFrame(rdd)
          val jaccardCalculated = jaccardProcessor.process(documentsViewDF)
          storageDocuments.storeOrUpdate(jaccardCalculated)
    })

    println("calculate batch stream")
    ssc.start()
    ssc.awaitTermination()
  }

  def convertToDataFrame(rdd: RDD[DocumentView])={
    import spark.implicits._
    rdd.toDF()
  }
}
