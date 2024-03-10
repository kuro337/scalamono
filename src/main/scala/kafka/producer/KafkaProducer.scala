package kafka.producer

import org.apache.kafka.clients.producer.{
  KafkaProducer,
  ProducerRecord,
  ProducerConfig,
  RecordMetadata
}
import java.util.Properties
import scala.concurrent.{Future, ExecutionContext}

//  https://docs.confluent.io/platform/current/installation/configuration/producer-configs.html

class KafkaMessageProducer(bootstrapServers: String)(implicit
    ec: ExecutionContext
) {
  private val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
  props.put(
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer"
  )
  props.put(
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
    "org.apache.kafka.common.serialization.StringSerializer"
  )

  private val producer = new KafkaProducer[String, String](props)

  def send(topic: String, key: String, value: String): Future[RecordMetadata] =
    Future {
      val record = new ProducerRecord[String, String](topic, key, value)
      producer
        .send(record)
        .get() // Note: Consider handling send asynchronously and managing potential blocking / exceptions
    }

  def close(): Unit = producer.close()
}
