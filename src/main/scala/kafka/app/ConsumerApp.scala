package kafka.app

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
  val bootstrapServers = "kuro.com:9094"
  val topicName = "test-topic"
  val groupId = "test"

  val client = new KafkaAdminClient(bootstrapServers)

  val groups = client.listConsumerGroups()
  groups.foreach(println)

//   client.deleteConsumerGroups(groups.map { _.groupId }.toList)

  val consumer = new KafkaConsumerGroup(bootstrapServers, groupId, topicName)

  consumer.subscribeAndPoll()

//  consumer.consumeMessagesAndHandleOffsetExit()

//   client.increasePartitions(topicName, 2)

  /*
   client.listTopics().foreach(println)

  val numPartitions = client.getTopicPartitionCount(topicName)
  println(s"Topic $topicName has $numPartitions partitions.")

  // client.resetConsumerGroupOffsets(groupId)

  // List Offsets
  val offsets = client.listConsumerGroupOffsets(groupId)
  offsets.asScala.foreach(println)

  // Describe the Consumer Group
  val desc = client.describeConsumerGroup(groupId)

  println(desc)

   */

  // consumer.consumeMessagesAndHandleOffset()

  // Create consumer configuration

  // val consumerConfig = Map[String, Object](
  //   ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers,
  //   ConsumerConfig.GROUP_ID_CONFIG -> groupId,
  //   ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
  //   ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[
  //     StringDeserializer
  //   ],
  //   ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> "earliest"
  // )

  // // Create Kafka consumer
  // val consumer = new KafkaConsumer[String, String](consumerConfig.asJava)

  // // Subscribe to the topic
  // consumer.subscribe(List(topicName).asJava)

  // // Consume messages
  // while (true) {
  //   val records = consumer.poll(java.time.Duration.ofMillis(100))
  //   for (record <- records.asScala) {
  //     println(s"Received message: ${record.value()}")
  //   }
  // }

  // // Close the consumer
  // consumer.close()
}
