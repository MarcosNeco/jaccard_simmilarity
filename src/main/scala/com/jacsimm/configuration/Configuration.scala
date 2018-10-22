package com.jacsimm.configuration

import org.apache.kafka.common.serialization.{LongDeserializer, LongSerializer, StringDeserializer, StringSerializer}

object Configuration {

  private val hostKafka = "localhost:9092"

  val KafkaProducerParams = Map[String, Object](
    "bootstrap.servers"-> hostKafka,
    "key.serializer" -> classOf[StringSerializer],
    "value.serializer" -> classOf[StringSerializer]
  )

  val kafkaConsumerParams = Map[String, Object](
   "metadata.broker.list" -> hostKafka,
    "bootstrap.servers"-> hostKafka,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "jacsimm.id",
    "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (false: java.lang.Boolean))

  val topicSet = Set[String]("view_doc")

  val intervalReadStream = 15

  val jaccardSimilarityTmpTable = "jaccard_similarity_tmp_table"

  val messageSeparator = ";"
}
