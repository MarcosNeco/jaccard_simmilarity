package com.jacsimm.consumer

import com.jacsimm.configuration.Configuration
import com.jacsimm.core.JaccardSimilarityProcessor
import com.jacsimm.session.SparkBuilderSession
import com.jacsimm.store.DocumentsRelationshipStore
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

object ConsumerDocView {

  val spark = new SparkBuilderSession().build()

  def startConsume={
    println("launcher stream application processor...")
    DocumentsRelationshipStore.createEmptyHistoricalData()
    val ssc = new StreamingContext(spark.sparkContext, Seconds(Configuration.intervalReadStream))

    val message = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Configuration.topicSet, Configuration.kafkaConsumerParams))

    val processor = new JaccardSimilarityProcessor()
    processor.process(message)
    println("calculate batch stream")
    ssc.start()
    ssc.awaitTermination()
  }
}
