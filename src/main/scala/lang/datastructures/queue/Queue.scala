package com.kuro.datastructures.queue

import scala.collection.immutable.Queue

object QueueScala {
  def qUsage(): Unit = {

    val queue: Queue[Int] = Queue()

    val enqueuedQueue = queue.enqueue(1)

    val newQueue = queue :+ 1

    // Return Element and new Q
    val (element, afterDequeueQueue) =
      newQueue.dequeue

    val firstElement = newQueue.head

  }
}
