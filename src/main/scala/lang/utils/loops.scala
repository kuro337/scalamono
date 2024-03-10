package lang.utils

object Loop {
  def main(): Unit = {
    val arr = Array(1, 2, 4, 10, 12)

    for (e <- arr) {
      println(e)
    }

    for (i <- 0 to arr.size) println(i) // Includes end
    for (i <- 0 until arr.size) println(i) // Excludes end

    // Iterating with Index

    arr.zipWithIndex.foreach { case (v, i) => println(s"Item:$v Index:$i") }
    for (i <- arr.indices) { println(s"Item:${arr(i)} Index:$i") }

    // .tail -> returns the "rest" of the list , excluding first elem
    val accProductLeft = arr.scanLeft(1)(_ * _).tail

    // .init -> returns the "initial" part of the segment , so removes last extra elem
    val accProductRight = arr.scanRight(1)(_ * _).init

    arr.foreach(println)

    arr.map(element => element * 2).foreach(println)

    val candies = Array(1, 5, 10, 15, 20, 25)
    val isDivBy5 = candies.map(e => e % 5 == 0).toList
    isDivBy5.foreach(println)

    val a = 5
    val b = 10
    val maxVal = a max b // or Math.max(a, b)
    println(maxVal) // Outputs: 10
  }
}
