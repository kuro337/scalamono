package kafka.client

import org.apache.kafka.clients.admin.{
  AdminClient,
  NewTopic,
  DeleteTopicsResult
}

import java.util.Properties
import org.joda.time.DateTime

import java.util.Collection

import scala.concurrent.{Future, ExecutionContext}
import scala.collection.JavaConverters._

import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.TopicDescription
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.ElectionType;
import org.apache.kafka.common.TopicPartitionInfo
import org.apache.kafka.common.errors.ElectionNotNeededException;
import org.apache.kafka.common.errors.InvalidPartitionsException;
import org.apache.kafka.common.errors.UnknownMemberIdException;

import org.apache.kafka.common.TopicPartition
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import java.util.HashMap

import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.NewPartitions
import org.apache.kafka.clients.admin.RecordsToDelete

/* Example https://github.com/gwenshap/kafka-examples/blob/master/AdminClientExample/src/main/java/org/example/AdminClientExample.java */

class KafkaAdminClient(bootstrapServers: String)(implicit
    ec: ExecutionContext
) {
  private val client: AdminClient = createAdminClient()

  private def createAdminClient(): AdminClient = {
    val props = new Properties()
    props.put("bootstrap.servers", bootstrapServers)
    AdminClient.create(props)
  }

  def close(): Unit = client.close()

  def listTopics(): Future[Set[String]] = Future {
    val topics = client.listTopics().names().get()
    topics.asScala.toSet
  }

  def createTopic(
      name: String,
      partitions: Int = 1,
      replicationFactor: Short = 1
  ): Future[Unit] = Future {
    val topic = new NewTopic(name, partitions, replicationFactor)
    client.createTopics(List(topic).asJavaCollection).all().get()
  }

  /** Writes a DataFrame to a CSV File with optional overwrite.
    * @param dest
    *   The destination path where the DataFrame will be written.
    * @param df
    *   The DataFrame to write.
    * @param overwriteIfExists
    *   Boolean indicating whether to overwrite existing files at the
    *   destination.
    * @example
    *   {{{Write.writeCSV(path, renamedDF, overwriteIfExists = true)}}}
    */
  def deleteTopic(name: String): Future[Unit] = Future {
    client.deleteTopics(List(name).asJava).all().get()
  }

  /** Get the Number of Partitions for a Topic.
    * @param topicName
    *   Name of the Topic
    * @return
    *   Int - Number of Partitions
    * @example
    *   {{{val numPartitions = client.getTopicPartitionCount(topicName)
    *   println(s"Topic $topicName has $numPartitions partitions.")}}}
    */
  def getTopicPartitionCount(topicName: String): Int = {
    val result = client.describeTopics(Seq(topicName).asJava)
    val values = result.topicNameValues()
    val topicDescription = values.get(topicName)
    val partitions = topicDescription.get().partitions().size()
    partitions
  }

  /* Lists each Consumer Group attached to the Cluster */
  def listConsumerGroups(): Iterable[ConsumerGroupListing] = {
    client.listConsumerGroups().valid().get().asScala
    // forEach(println);
  }

  def describeConsumerGroup(groupId: String): ConsumerGroupDescription = {
    // Describe a group
    client
      .describeConsumerGroups(List(groupId).asJava)
      .describedGroups()
      .get(groupId)
      .get()

  }

  /* Get offsets committed by the group */
  def listConsumerGroupOffsets(
      groupId: String
      // ): java.util.Map[TopicPartition, OffsetAndMetadata] = {
  ): java.util.Map[TopicPartition, OffsetAndMetadata] = {
    client
      .listConsumerGroupOffsets(groupId)
      .partitionsToOffsetAndMetadata()
      .get()

  }
  // To view Consumer Group Offsets (given a Group ID)
  // https://www.baeldung.com/java-kafka-consumer-lag

  def resetConsumerGroupOffsets(consumerGroup: String): Unit = {

    val offsets = listConsumerGroupOffsets(consumerGroup)

    val requestLatestOffsets = new HashMap[TopicPartition, OffsetSpec]
    val requestEarliestOffsets = new HashMap[TopicPartition, OffsetSpec]
    val requestOlderOffsets = new HashMap[TopicPartition, OffsetSpec]

    val resetTo = new DateTime().minusHours(2);

    offsets.keySet().asScala.foreach { tp =>
      requestLatestOffsets.put(tp, OffsetSpec.latest())
      requestEarliestOffsets.put(tp, OffsetSpec.earliest())
      requestOlderOffsets.put(tp, OffsetSpec.forTimestamp(resetTo.getMillis))
    }

    val latestOffsets = client.listOffsets(requestLatestOffsets).all().get();

    latestOffsets.asScala.foreach { case (topicPartition, offsetAndMetadata) =>
      val topic = topicPartition.topic
      val partition = topicPartition.partition
      val committedOffset = offsetAndMetadata.offset()
      val latestOffset = latestOffsets.get(topicPartition).offset()

      System.out.println(
        "Consumer group " + consumerGroup
          + " has committed offset " + committedOffset
          + " to topic " + topic + " partition " + partition
          + ". The latest offset in the partition is "
          + latestOffset + " so consumer group is "
          + (latestOffset - committedOffset) + " records behind"
      );

    }

    // Reset Offsets to Earliest
    val earliestOffsets = client.listOffsets(requestEarliestOffsets).all().get()

    val resetOffsets = new HashMap[TopicPartition, OffsetAndMetadata]

    earliestOffsets.asScala.foreach { case (topicPartition, offsetInfo) =>
      println(
        s"Will reset topic-partition $topicPartition to offset ${offsetInfo.offset()}"
      )
      resetOffsets.put(
        topicPartition,
        new OffsetAndMetadata(offsetInfo.offset())
      )
    }

    try {
      client
        .alterConsumerGroupOffsets(consumerGroup, resetOffsets)
        .all()
        .get()
    } catch {
      case e: ExecutionException =>
        println(
          s"Failed to update the offsets committed by group $consumerGroup with error ${e.getMessage}"
        )
        if (e.getCause.isInstanceOf[UnknownMemberIdException])
          println("Check if consumer group is still active.")
    }

  }

  /** Increases partitions to a Kafka topic.
    *
    * if the number of partitions cannot be modified.
    * [[https://github.com/gwenshap/kafka-examples/blob/master/AdminClientExample/src/main/java/org/example/AdminClientExample.java Kafka Clients Java Source Code]]
    *
    * @param topicName
    *   The name of the topic to add partitions to.
    * @param numPartitions
    *   The current number of partitions in the topic.
    * @throws ExecutionException
    *   if an error occurs while adding partitions.
    * @throws InvalidPartitionsException
    *
    * @example
    *   {{{client.increasePartitions(topicName, 2)}}}
    */
  def increasePartitions(topicName: String, newPartitionCount: Int): Unit = {
    val newPartitions = new HashMap[String, NewPartitions]
    newPartitions.put(
      topicName,
      NewPartitions.increaseTo(newPartitionCount)
    )

    try {
      client.createPartitions(newPartitions).all().get()
      println(
        s"Successfully increased the number of partitions for topic '$topicName' to $newPartitionCount"
      )
    } catch {
      case ex: ExecutionException =>
        ex.getCause() match {
          case _: InvalidPartitionsException =>
            println(
              s"Couldn't modify the number of partitions for topic '$topicName': ${ex.getMessage}"
            )
          case _ =>
            throw ex
        }
    }
  }

  def deleteRecords(
      topicName: String,
      consumerGroup: String,
      deleteCutoff: DateTime
  ): Unit = {
    def deleteRecords(consumerGroup: String, deleteCutoff: DateTime): Unit = {
      val offsets = listConsumerGroupOffsets(consumerGroup)
      val requestOlderOffsets = new HashMap[TopicPartition, OffsetSpec]
      offsets.keySet().asScala.foreach { tp =>
        requestOlderOffsets
          .put(tp, OffsetSpec.forTimestamp(deleteCutoff.getMillis))
      }

      val olderOffsets = client.listOffsets(requestOlderOffsets).all().get()
      val recordsToDelete = new HashMap[TopicPartition, RecordsToDelete]

      olderOffsets.asScala.foreach { case (topicPartition, offsetInfo) =>
        recordsToDelete.put(
          topicPartition,
          RecordsToDelete.beforeOffset(offsetInfo.offset())
        )
      }

      client.deleteRecords(recordsToDelete).all().get()
    }
  }

  /** Delete Consumer Groups
    * @param dest
    *   The destination path where the DataFrame will be written.
    * @param df
    *   The DataFrame to write.
    * @param overwriteIfExists
    *   Boolean indicating whether to overwrite existing files at the
    *   destination.
    * @example
    *   {{{val groups = client.listConsumerGroups() groups.foreach(println)
    *   client.deleteConsumerGroups(groups.map { _.groupId }.toList)}}}
    */
  def deleteConsumerGroups(groupIds: List[String]): Unit = {
    try {
      client.deleteConsumerGroups(groupIds.asJava).all().get()
      println(
        s"Successfully deleted consumer groups: ${groupIds.mkString(", ")}"
      )
    } catch {
      case ex: ExecutionException =>
        val causeMessage = ex.getCause match {
          case null  => ""
          case cause => s": ${cause.getMessage}"
        }
        println(
          s"Failed to delete consumer groups: ${groupIds.mkString(", ")}$causeMessage"
        )
    }
  }
}
