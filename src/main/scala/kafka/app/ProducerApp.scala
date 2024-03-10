package kafka.app

import kafka.producer.KafkaMessageProducer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object ProducerApp extends App {
  val bootstrapServers = "kuro.com:9094"
  val topic = "test-topic"
  val producer = new KafkaMessageProducer(bootstrapServers)

  // Example of sending a single message
  val futureResult = producer.send(topic, "key1", "Hello, Kafka!")

  futureResult.onComplete {
    case Success(metadata) =>
      println(s"Sent message to topic ${metadata.topic()} partition ${metadata
          .partition()} with offset ${metadata.offset()}")
    case Failure(exception) =>
      println(s"Failed to send message: ${exception.getMessage}")
  }

  // Ensure the application waits for the message to be sent before exiting
  Thread.sleep(2000)

  // Don't forget to close the producer to free resources
  producer.close()
}
