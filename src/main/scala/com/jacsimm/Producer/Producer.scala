package com.jacsimm.Producer

import com.jacsimm.configuration.Configuration
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object Producer {

  def publish(document:Long, user:String){
    import scala.collection.JavaConverters._
    val producer = new KafkaProducer[Long, String](Configuration.KafkaProducerParams.asJava)
    val record = new ProducerRecord[Long, String](Configuration.topicSet.head, document, user)
    producer.send(record)
  }
}
