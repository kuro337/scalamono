package lang.datastructures.pq

import scala.collection.mutable.PriorityQueue

object PQScala {

  case class Person(name: String, age: Int)

  def prioqObj(): Unit = {

    /* implicit ordering is Compiler Detected wherever Ordering is used */

    implicit val personOrdering: Ordering[Person] = Ordering.by(_.age)

    val reversePersonOrdering: Ordering[Person] = personOrdering.reverse

    implicit val stringLengthOrdering: Ordering[String] = Ordering.by(_.length)

    val complexPersonOrdering: Ordering[Person] =
      Ordering.by[Person, (Int, Int)](p => (p.name.length, p.age))

    /* Explicitly Pass or it is determined */

    var stringPQ = PriorityQueue("apple", "banana", "fig", "date")

    // Adding a full List to a stringPQ
    val items = List("A", "B", "C", "D")

    stringPQ ++= items

    val personPQ = PriorityQueue(
      Person("Alice", 30),
      Person("Bob", 25),
      Person("Charlie", 35)
    )

    // Bob would be highest
    val reversePersonPQ = PriorityQueue(
      Person("Alice", 30),
      Person("Bob", 25),
      Person("Charlie", 35)
    )

    // The priority is determined first by name length, then by age.
    val complexPersonPQ = PriorityQueue(
      Person("Alice", 30),
      Person("Bob", 20),
      Person("Charlie", 35),
      Person("Dave", 20)
    )(complexPersonOrdering)

  }

  def pqUsage(): Unit = {

    /* Priority Queue with an ordering for integers (Max Heap by default) */

    // implicit ordering is Compiler Detected wherever Ordering is used

    implicit val minOrdering: Ordering[Int] = Ordering.Int.reverse

    val prioq = PriorityQueue(10, 2, 20)
    prioq += 5

    val top = prioq.head

    val removed = prioq.dequeue

    val size = prioq.size

    val isEmpty = prioq.isEmpty

    prioq += 99

    prioq ++= List(9, 10, 11, 12)

    // For Max Heap -> reverse the comparison
    val pq: PriorityQueue[Int] = PriorityQueue.empty(Ordering.Int.reverse)

    // Adding elements
    pq.enqueue(5)
    pq.enqueue(1)
    pq.enqueue(3)

    // Examine the head of the queue (the largest element in this max heap configuration) without removing it
    println(s"Head of queue: ${pq.head}")

    // Iterating over elements (not in any specific order because PriorityQueue does not guarantee iteration order)
    println("Elements in queue:")
    pq.foreach(println)

    // Removing elements (in descending order due to max heap configuration)
    println("Removing elements:")
    while (pq.nonEmpty) {
      println(pq.dequeue())
    }

    // PriorityQueue also supports bulk additions
    pq ++= Seq(4, 2, 6)

    // Examining and removing the top element in a single operation
    if (pq.nonEmpty) {
      println(s"Removed element: ${pq.dequeue()}")
    }
  }
}
