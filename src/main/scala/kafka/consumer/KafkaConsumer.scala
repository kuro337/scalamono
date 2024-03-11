package kafka.consumer
import utils.signals.ShutdownListener

import java.time.Duration
import java.util.Properties
import java.util.Collections
import org.apache.kafka.clients.consumer.{
  ConsumerConfig,
  KafkaConsumer,
  ConsumerRecords,
  OffsetAndMetadata,
  OffsetCommitCallback
}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.clients.consumer.ConsumerRecord
import scala.collection.JavaConverters._

/*
1 Consumer Group can have multiple Consumers
1 Topic -> 1 or >1 Partitions
1 Partition can only be consumed by 1 Consumer

1. Creating 1 Consumer to Consume from a Topic
  val consumer = new KafkaMessageConsumer("localhost:9092", "my-group", "my-topic")
  consumer.consumeMessagesAndHandleOffsetExit()

2. Creating 2 Consumers for a Consumer Group for a Topic:
  val consumer1 = new KafkaMessageConsumer("localhost:9092", "my-group", "my-topic")
  val consumer2 = new KafkaMessageConsumer("localhost:9092", "my-group", "my-topic")
//// Start consuming messages in separate threads or processes
consumer1.consumeMessagesAndHandleOffset()
consumer2.consumeMessagesAndHandleOffset()
 */

class KafkaConsumerGroup(
    bootstrapServers: String,
    groupId: String,
    topic: String
) {

@volatile private var keepRunning = true

  private val consumer = ConsumerGroupConfig.createConsumerGroup(bootstrapServers,groupId)

  // Graceful Shutdown Listener
  private val shutdownListener = new ShutdownListener(() => {
    keepRunning = false
    println("Shutdown command received, exiting...")
  })



  def close():Unit = consumer.close()
  
  def subscribeAndPoll() {
         shutdownListener.start()

      consumer.subscribe(Collections.singletonList(topic))
      consumeMessagesAndHandleOffset()
  }


    def consumeMessagesAndHandleOffsetExit(
      pollInterval: Long = 100,
      exitThreshold: Long = 5000
  ): Unit = {
    println(
      s"Polling Topic $topic every $pollInterval ms. Exiting after no events consumed for $exitThreshold ms."
    )
    val startTime = System.currentTimeMillis()

    try {
      var latestRecordTime = startTime
      var emptyRecordTime = startTime
      var recordsFound = false

      while (
        !recordsFound || !calcTimeList(
          emptyRecordTime,
          latestRecordTime,
          exitThreshold
        )
      ) {

        val records: ConsumerRecords[String, String] =
          consumer.poll(Duration.ofMillis(pollInterval))

          val currTime = System.currentTimeMillis()

          if (!records.isEmpty) {
            processRecords(records)
            manuallyCommitOffsetsAsync()
            emptyRecordTime = currTime
            latestRecordTime = currTime
            recordsFound = true
          } else {
            emptyRecordTime = currTime
          }
      }
    } finally {
      consumer.close()
    }
  }


  

  def consumeMessagesAndHandleOffset(): Unit = {

    try {
      while (keepRunning) {
        val records: ConsumerRecords[String, String] =
          consumer.poll(Duration.ofMillis(1000))
          if (!records.isEmpty) {
            processRecords(records)
            manuallyCommitOffsetsAsync()
          }
      }
    } finally {
      consumer.close()
    }
  }



    private def manuallyCommitOffsetsAsync(): Unit = {
    consumer.commitAsync(new OffsetCommitCallback() {
      override def onComplete(
          offsets: java.util.Map[TopicPartition, OffsetAndMetadata],
          exception: Exception
      ): Unit = {
        if (exception != null) {
          println("Failed to commit offsets: " + exception.getMessage)
        } else {
          println("Offsets have been committed")
        }
      }
    })
  }

    private def processRecords(records: ConsumerRecords[String, String]): Unit = {
    records.asScala.foreach { record =>
      println(
        s"Consumed message with key: ${record.key()}, value: ${record.value()}, partition: ${record
            .partition()}, offset: ${record.offset()}"
      )
    }
  }



   @inline private def calcTimeList( bigger: Long, shorter: Long, threshold: Long ): Boolean =
      bigger - shorter > threshold
  


}

