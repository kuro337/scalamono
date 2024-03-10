package kafka.connect

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object KafkaConnect {

  val props = new java.util.Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put(
    "key.serializer",
    "org.apache.kafka.common.serialization.StringSerializer"
  )
  props.put(
    "value.serializer",
    "org.apache.kafka.common.serialization.StringSerializer"
  )

  val producer = new KafkaProducer[String, String](props)

  val record = new ProducerRecord[String, String]("your_topic", "key", "value")
  producer.send(record)
  producer.close()

}
