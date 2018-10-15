package com.jacsimm.configuration

import org.apache.kafka.common.serialization.StringDeserializer

object Configuration {

  val kafkaParams = Map[String, Object](
    "bootstrap.servers"-> "localhost:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "jacsimm.id",
    "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (false: java.lang.Boolean))

  val topicSet =
    Set[String]("view_doc")
}
