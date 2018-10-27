package com.com.jacsimm.core

import java.util
import java.util.Properties

import com.holdenkarau.spark.testing.StreamingSuiteBase
import com.jacsimm.configuration.Configuration
import com.jacsimm.core.JaccardSimilarityProcessor
import com.jacsimm.store.DocumentsRelationshipStore
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.record.TimestampType
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.spark.streaming.dstream.DStream
import org.scalatest.{BeforeAndAfter, FunSuite}
import org.apache.kafka.streams.test.ConsumerRecordFactory
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.{StreamsBuilder, StreamsConfig, Topology, TopologyTestDriver}
import org.apache.kafka.streams.state.Stores

class JaccardSimilarityProcessorTest extends FunSuite with BeforeAndAfter with StreamingSuiteBase{

  private val testDriver = null
  private val store = null

  private val stringDeserializer = new StringDeserializer
  private val longDeserializer = new StringDeserializer
  private val recordFactory = new ConsumerRecordFactory[String, String](new StringSerializer, new StringSerializer)


  before{

    val topology = new TopologyTestDriver()
    topology.addSource("sourceProcessor", "input-topic")
    topology.addProcessor("aggregator", new Nothing, "sourceProcessor")
    topology.addStateStore(Stores.keyValueStoreBuilder(Stores.inMemoryKeyValueStore("aggStore"), Serdes.String, Serdes.Long).withLoggingDisabled, // need to disable logging to allow store pre-populating
      "aggregator")
    topology.addSink("sinkProcessor", "result-topic", "aggregator")

    val config = new Properties()
    config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "maxAggregation")
    config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")
    config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long.getClass.getName)
    testDriver = new TopologyTestDriver((topology, config)

    // pre-populate store
    store = testDriver.getKe("aggStore")

  }

  test("valid two docs with total correlation"){
    val records = new util.LinkedHashMap[TopicPartition, java.util.List[ConsumerRecord[String, String]]]

    val topic = Configuration.topicSet.head
    val record1 = new ConsumerRecord(topic, 1, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, "1", "value1")
    val record2 = new ConsumerRecord(topic, 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, "1", "value2")
    records.put(new TopicPartition(topic, 1), util.Arrays.asList(record1, record2))

    import org.apache.kafka.common.serialization.Serde
    import org.apache.kafka.common.serialization.Serdes
    import org.apache.kafka.streams.kstream.Consumed
    import org.apache.kafka.streams.kstream.KStream
    val stringSerde = Serdes.String
    val longSerde = Serdes.Long

    val textLines = new StreamsBuilder().stream("streams-plaintext-input", Consumed.`with`(stringSerde, stringSerde))

    val processor = new JaccardSimilarityProcessor()

    processor.process(textLines, sparkSession)

    val topSimilar = DocumentsRelationshipStore.getTop10Similar()
    assertResult(1L )(topSimilar.get(0).getDocumentA)
    assertResult(2L )(topSimilar.get(0).getDocumentB)
    assertResult(1F )(topSimilar.get(0).getJaccardIndex)
  }

  after{

  }

}
