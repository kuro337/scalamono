package lang.async

import scala.concurrent.{Future, ExecutionContext}
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object AsyncExamples {

  def calculateAsync(): Future[Int] = Future {
    Thread.sleep(
      1000
    )
    42
  }

  // Example 2: Transforming Futures with map
  def transformFuture(): Future[String] = {
    val numberFuture: Future[Int] = Future { 21 + 21 }
    numberFuture.map(answer => s"The answer is $answer")
  }

  // Example 3: Chaining Futures with flatMap
  def chainFutures(): Future[String] = {
    val firstFuture: Future[Int] = Future { 20 }
    val secondFuture: Future[String] = firstFuture.flatMap { result =>
      Future { (result * 2).toString }
    }
    secondFuture
  }

  // Example 4: Handling Future results with onComplete
  def handleFutureResult(): Unit = {
    val future: Future[Int] = Future { 21 + 21 }
    future.onComplete {
      case Success(value)     => println(s"Result: $value")
      case Failure(exception) => println(s"Failed with exception: $exception")
    }
  }

  // Example 5: Using for-comprehensions with Futures
  def forComprehensionExample(): Future[String] = {
    val firstFuture: Future[Int] = Future { 21 }
    val secondFuture: Future[Int] = Future { 21 }

    // Compose futures in a for-comprehension
    for {
      firstResult <- firstFuture
      secondResult <- secondFuture
    } yield s"The total is ${firstResult + secondResult}"
  }

  // Example 6: Recovering from failures
  def recoverFromFailure(): Future[Int] = {
    val riskyFuture: Future[Int] = Future { throw new Exception("Oops!") }

    riskyFuture.recover { case _: Exception =>
      0 // Provide a default value in case of failure
    }
  }
}
