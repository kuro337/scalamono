package kafka.consumer

import org.apache.kafka.clients.consumer.{
  ConsumerConfig,
  ConsumerRecord,
  KafkaConsumer
}
import org.apache.kafka.common.serialization.StringDeserializer
import scala.collection.JavaConverters._

sealed trait OffsetStrategy
case object Earliest extends OffsetStrategy
case object Latest extends OffsetStrategy
case object NoOffset extends OffsetStrategy

sealed trait HealthCheck
case class Heartbeat(intervalMs: Int) extends HealthCheck
case class SessionTimeout(durationMs: Int) extends HealthCheck

sealed trait Polling
case class PollInterval(intervalMs: Long) extends Polling
case class PollRecords(maxRecords: Int) extends Polling

object ConsumerGroupConfig {

  def createConsumerGroup(
      bootstrapServers: String,
      groupId: String,
      offsetStrategy: OffsetStrategy = Latest,
      healthCheck: Option[HealthCheck] = None,
      polling: Option[Polling] = None,
      autoCreateTopics: Boolean = true
  ): KafkaConsumer[String, String] = {

    val consumerConfig = ConsumerGroupConfig.createConsumerConfig(
      bootstrapServers,
      groupId,
      offsetStrategy,
      healthCheck,
      polling,
      autoCreateTopics
    )

    new KafkaConsumer[String, String](consumerConfig.asJava)

  }

  def createConsumerConfig(
      bootstrapServers: String,
      groupId: String,
      offsetStrategy: OffsetStrategy = Latest,
      healthCheck: Option[HealthCheck] = None,
      polling: Option[Polling] = None,
      autoCommit: Boolean = false,
      autoCreateTopics: Boolean = true
  ): Map[String, Object] = {
    val baseConfig = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> bootstrapServers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[
        StringDeserializer
      ].getName,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[
        StringDeserializer
      ].getName,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG -> offsetStrategyToStr(
        offsetStrategy
      ),
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG -> autoCommit.toString,
      ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG -> autoCreateTopics.toString
    )

    val healthCheckConfig = healthCheck match {
      case Some(Heartbeat(interval)) =>
        Map(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG -> interval.toString)
      case Some(SessionTimeout(duration)) =>
        Map(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG -> duration.toString)
      case None => Map.empty[String, Object]
    }

    val pollingConfig = polling match {
      case Some(PollInterval(interval)) =>
        Map(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG -> interval.toString)
      case Some(PollRecords(maxRecords)) =>
        Map(ConsumerConfig.MAX_POLL_RECORDS_CONFIG -> maxRecords.toString)
      case None => Map.empty[String, Object]
    }

    baseConfig ++ healthCheckConfig ++ pollingConfig
  }

  private def offsetStrategyToStr(strategy: OffsetStrategy): String =
    strategy match {
      case Earliest => "earliest"
      case Latest   => "latest"
      case NoOffset =>
        "none" // Updated to reflect the renamed OffsetStrategy case
    }
}

/*
https://docs.confluent.io/platform/current/installation/configuration/consumer-configs.html

https://www.conduktor.io/kafka/kafka-consumer-important-settings-poll-and-internal-threads-behavior/



 */

// auto.offset.reset
// What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted):

// earliest: automatically reset the offset to the earliest offset
// latest: automatically reset the offset to the latest offset
// none: throw exception to the consumer if no previous offset is found for the consumer’s group
// anything else: throw exception to the consumer.
// Note that altering partition numbers while setting this config to latest may cause message delivery loss since producers could start to send messages to newly added partitions (i.e. no initial offsets exist yet) before consumers reset their offsets.

/** Kafka Consumer Poll Thread
  *
  * Consumers poll brokers periodically using the .poll() method. If two .poll()
  * calls are separated by more than max.poll.interval.ms time, then the
  * consumer will be disconnected from the group.
  */

////

/*
group.id

key.deserializer
Deserializer class for key that implements the org.apache.kafka.common.serialization.Deserializer interface.

value.deserializer
Deserializer class for value that implements the org.apache.kafka.common.serialization.Deserializer interface.

fetch.min.bytes (Default = 1 Byte)
Min Amount of Data to Return for each Fetch.
Default is 1 Byte - but we can improve throughput by trading off Latency.

###
session.timeout.ms is used to detect failures and manage the group membership of
consumers within a consumer group.
If a broker does not receive a heartbeat from a Consumer within session.timeout.ms -
the consumer is removed from the group and the cluster triggers a Rebalance of the Partitions!
###

> heartbeat.interval.ms
The expected time between heartbeats to the consumer coordinator when using Kafka’s group management facilities. Heartbeats are used to ensure that the consumer’s session stays active and to facilitate rebalancing when new consumers join or leave the group. The value must be set lower than session.timeout.ms, but typically should be set no higher than 1/3 of that value. It can be adjusted even lower to control the expected time for normal rebalances.
- Type:	int
Default:	3000 (3 seconds)
Valid Values:
Importance:	high

> session.timeout.ms
- The timeout used to detect client failures when using Kafka’s group management facility. The client sends periodic heartbeats to indicate its liveness to the broker. If no heartbeats are received by the broker before the expiration of this session timeout, then the broker will remove this client from the group and initiate a rebalance. Note that the value must be in the allowable range as configured in the broker configuration by group.min.session.timeout.ms and group.max.session.timeout.ms.
Type:	int
Default:	45000 (45 seconds)
Valid Values:
Importance:	high

max.partition.fetch.bytes
The maximum amount of data per-partition the server will return. Records are fetched in batches by the consumer. If the first record batch in the first non-empty partition of the fetch is larger than this limit, the batch will still be returned to ensure that the consumer can make progress. The maximum record batch size accepted by the broker is defined via message.max.bytes (broker config) or max.message.bytes (topic config). See fetch.max.bytes for limiting the consumer request size.

Type:	int
Default:	1048576 (1 mebibyte)
Valid Values:	[0,…]
Importance:	high

allow.auto.create.topics
Allow automatic topic creation on the broker when subscribing to or assigning a topic. A topic being subscribed to will be automatically created only if the broker allows for it using auto.create.topics.enable broker configuration. This configuration must be set to false when using brokers older than 0.11.0

Type:	boolean
Default:	true
Valid Values:
Importance:	medium


auto.offset.reset
What to do when there is no initial offset in Kafka or if the current offset does not exist any more on the server (e.g. because that data has been deleted):

earliest: automatically reset the offset to the earliest offset
latest: automatically reset the offset to the latest offset
none: throw exception to the consumer if no previous offset is found for the consumer’s group
anything else: throw exception to the consumer.
Note that altering partition numbers while setting this config to latest may cause message delivery loss since producers could start to send messages to newly added partitions (i.e. no initial offsets exist yet) before consumers reset their offsets.

Type:	string
Default:	latest
Valid Values:	[latest, earliest, none]
Importance:	medium

partition.assignment.strategy
A list of class names or class types, ordered by preference, of supported partition assignment strategies that the client will use to distribute partition ownership amongst consumer instances when group management is used. Available options are:

org.apache.kafka.clients.consumer.RangeAssignor: Assigns partitions on a per-topic basis.
org.apache.kafka.clients.consumer.RoundRobinAssignor: Assigns partitions to consumers in a round-robin fashion.
org.apache.kafka.clients.consumer.StickyAssignor: Guarantees an assignment that is maximally balanced while preserving as many existing partition assignments as possible.
org.apache.kafka.clients.consumer.CooperativeStickyAssignor: Follows the same StickyAssignor logic, but allows for cooperative rebalancing.
The default assignor is [RangeAssignor, CooperativeStickyAssignor], which will use the RangeAssignor by default, but allows upgrading to the CooperativeStickyAssignor with just a single rolling bounce that removes the RangeAssignor from the list.

Implementing the org.apache.kafka.clients.consumer.ConsumerPartitionAssignor interface allows you to plug in a custom assignment strategy.

Type:	list
Default:	class org.apache.kafka.clients.consumer.RangeAssignor,class org.apache.kafka.clients.consumer.CooperativeStickyAssignor
Valid Values:	non-null string
Importance:	medium


security.protocol
Protocol used to communicate with brokers. Valid values are: PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL.

Type:	string
Default:	PLAINTEXT
Valid Values:	(case insensitive) [SASL_SSL, PLAINTEXT, SSL, SASL_PLAINTEXT]
Importance:	medium
 */
