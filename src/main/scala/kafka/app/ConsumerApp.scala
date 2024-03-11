package kafka.app

import kafka.constants.Constants
import kafka.client.KafkaAdminClient
import kafka.consumer.KafkaConsumerGroup

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

import org.apache.kafka.clients.consumer.{
  ConsumerConfig,
  KafkaConsumer,
  ConsumerRecords
}

import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.{Collections, Properties}
import scala.collection.JavaConverters._

import java.util.UUID

object ConsumerApp extends App {
  val topicName = "test"
  val groupId = "test-grp"

  val client = new KafkaAdminClient(Constants.RedpandaBootstrapServers)

  val groups = client.listConsumerGroups()
  groups.foreach(println)

  val consumer =
    new KafkaConsumerGroup(
      Constants.RedpandaBootstrapServers,
      groupId,
      topicName
    )

  /* Polling */
  consumer.subscribeAndPoll()
  consumer.consumeMessagesAndHandleOffsetExit()

  /* Increasing Partitions & Listing Metdata */
  client.increasePartitions(topicName, 2)
  client.listTopics().foreach(println)

  val numPartitions = client.getTopicPartitionCount(topicName)
  println(s"Topic $topicName has $numPartitions partitions.")

  client.resetConsumerGroupOffsets(groupId)

  /* List Offsets */
  val offsets = client.listConsumerGroupOffsets(groupId)
  offsets.asScala.foreach(println)

  /* Describe the Consumer Group */
  val desc = client.describeConsumerGroup(groupId)
  println(desc)

  consumer.consumeMessagesAndHandleOffset()

  client.deleteConsumerGroups(groups.map { _.groupId }.toList)

  /* Close the consumer */
  consumer.close()
}
