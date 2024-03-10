package utils.futures

import scala.concurrent.{Future, Promise}
import org.apache.kafka.common.KafkaFuture

import scala.concurrent.ExecutionContext.Implicits.global

object FutureUtils {

  def toScalaFuture[T](kafkaFuture: KafkaFuture[T]): Future[T] = {
    val promise = Promise[T]()
    kafkaFuture.whenComplete { (result, exception) =>
      if (exception != null) promise.failure(exception)
      else promise.success(result)
    }
    promise.future
  }

}
