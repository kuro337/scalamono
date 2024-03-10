package kafka.app

import kafka.client.KafkaAdminClient
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object OOPApp extends App {
  val kafkaClient = new KafkaAdminClient("kuro.com:9094")

  // Sequence the operations using for-comprehension
  val operations = for {
    _ <- kafkaClient.createTopic("test-topic")
    topics <- kafkaClient.listTopics()

  } yield topics

  // Handle the result of the operations
  operations.onComplete {
    case Success(topics) =>
      println(s"Topic creation successful. List of topics: $topics")
    case Failure(e) =>
      println(s"An error occurred: ${e.getMessage}")
  }

  // Await completion of all operations before closing the client.
  // Note: This is a blocking operation and is used here for simplicity.
  // In a production environment, consider using a more sophisticated approach to manage the lifecycle of asynchronous operations.
  Await.ready(operations, 10.seconds)

  // Close the client explicitly here, ensuring it's done after all futures are resolved.
  kafkaClient.close()
  println("KafkaAdminClient closed.")
}
