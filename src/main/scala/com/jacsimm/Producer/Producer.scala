package com.jacsimm.Producer

import com.jacsimm.configuration.Configuration
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object Producer {

  def publish(document:Long, user:String){
    import scala.collection.JavaConverters._
    val producer = new KafkaProducer[String, String](Configuration.KafkaProducerParams.asJava)
    val message = document + Configuration.messageSeparator + user
    val record = new ProducerRecord[String, String](Configuration.topicSet.head, "1", message)
    producer.send(record)
  }
}
