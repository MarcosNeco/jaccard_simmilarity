import java.util.Properties

import com.jacsimm.configuration.Configuration
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object Producer {


  val serializer = "org.apache.kafka.common.serialization.StringSerializer"

  val props = new Properties()
  props.put("metadata.broker.list", "localhost:9092")
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", serializer)
  props.put("value.serializer", serializer)

  val producer = new KafkaProducer[String, String](props)
  val topic = "view_doc"

  val record = new ProducerRecord(topic, "doc13","user10")
  producer.send(record)
}
