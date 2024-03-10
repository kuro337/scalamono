package kafka.app

import org.apache.kafka.clients.admin.{AdminClient, KafkaAdminClient}
import scala.jdk.CollectionConverters._
import java.util.Properties

import org.apache.kafka.clients.admin.AdminClient
import scala.jdk.CollectionConverters._

object KafkaClientExample {
  def main(args: Array[String]): Unit = {

    val brokerProperties = new Properties()
    brokerProperties.put("bootstrap.servers", "kuro.com:9094")

    val adminClient: AdminClient = AdminClient.create(brokerProperties)

    try {

      val clusterDescription = adminClient.describeCluster()

      val clusterId = clusterDescription.clusterId().get()
      println(s"Cluster ID: $clusterId")

      val controller = clusterDescription.controller().get()
      println(s"Controller: ${controller.host()}:${controller.port()}")

      val brokers = clusterDescription.nodes().get().asScala
      println("Brokers:")

      brokers.foreach { broker =>
        println(s"${broker.host()}:${broker.port()}")
      }
    } finally {
      // Close the admin client
      adminClient.close()
    }
  }
}
