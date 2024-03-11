package kafka.app

import kafka.client.KafkaAdminClient
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object OOPApp extends App {
  val kafkaClient = new KafkaAdminClient("kuro.com:9094")

  val operations = for {
    _ <- kafkaClient.createTopic("test-topic")
    topics <- kafkaClient.listTopics()

  } yield topics

  operations.onComplete {
    case Success(topics) =>
      println(s"Topic creation successful. List of topics: $topics")
    case Failure(e) =>
      println(s"An error occurred: ${e.getMessage}")
  }

  Await.ready(operations, 10.seconds)

  kafkaClient.close()
  println("KafkaAdminClient closed.")
}
