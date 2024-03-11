package kafka.app

import kafka.constants.Constants

import kafka.producer.KafkaMessageProducer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.Future

object ProducerApp extends App {

  val topic = "test"
  val producer = new KafkaMessageProducer(Constants.RedpandaBootstrapServers)

  val sampleEvents = Seq(
    ("key1", "Testing External Redpanda"),
    ("key2", "Testing Another Message"),
    ("key3", "Inserted from Sparkuro"),
    ("key4", "Hello Redpanda!"),
    ("key5", "Streaming Data")
  )

  /* Produce some Events */
  val inserts: Seq[Future[Unit]] = sampleEvents.map { case (key, value) =>
    producer
      .send(topic, key, value)
      .map { metadata =>
        println(s"Sent message to topic ${metadata.topic()} partition ${metadata
            .partition()} with offset ${metadata.offset()}")
      }
      .recover { case exception: Throwable =>
        println(s"Failed to send message: ${exception.getMessage}")
      }
  }

  val allInserts: Future[Seq[Unit]] = Future.sequence(inserts)
  Await.ready(allInserts, 10.seconds)

  /* Produce a Single Event */
  val futureResult = producer.send(topic, "Single Send", "Hello, Kafka!")

  futureResult.onComplete {
    case Success(metadata) =>
      println(s"Sent message to topic ${metadata.topic()} partition ${metadata
          .partition()} with offset ${metadata.offset()}")
    case Failure(exception) =>
      println(s"Failed to send message: ${exception.getMessage}")
  }

  Thread.sleep(2000)

  producer.close()
}
