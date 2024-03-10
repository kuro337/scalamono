package com.kuro.datastructures.sortedset

import scala.collection.immutable.SortedSet

object SortedSetScala {
  def sortedSetUsage(): Unit = {
    val a = Array(10, 5, 9, 2, 22, 1, 4)

    val set = SortedSet(a: _*)

    val setWithMore = set + 5 + 6

    val setWithLess = set - 1 - 2

    set.foreach(println)

    val exists = set(3) // true if 3 is in the set, false otherwise

    // Finding the first (smallest) element
    val first = set.head

    // Finding the last (largest) element
    val last = set.last

    // Range selection (elements greater than 2)

    val rangeSelection = set.from(3)

    // Size of the set
    val size = set.size
  }
}
