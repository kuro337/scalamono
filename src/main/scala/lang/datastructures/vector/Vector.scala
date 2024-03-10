package com.kuro.datastructures.vector

/*
Vector is implemented as a Trie , so lookup is Constant

The Trie structure makes updating it efficient

Suitable Over a List when Fast Random Access is Required




 */
object VectorScala {
  def vectorUsage(): Unit = {}

  val vector = Vector(1, 2, 3, 4, 5)

  val firstElement = vector(0)
  val thirdElement = vector(2)

  val appendedVector = vector :+ 6

  val prependedVector = 0 +: vector

  val updatedVector = vector.updated(1, 10) // Update index 1 to 10

  /* Concatenating vectors */

  val anotherVector = Vector(6, 7, 8)

  val concatenatedVector = vector ++ anotherVector

  val squaredVector = vector.map(x => x * x)

  val evenVector = vector.filter(x => x % 2 == 0)

  val sum = vector.foldLeft(0)(_ + _)

  val product = vector.reduce(_ * _)

  val reversedVector = vector.reverse
  println(s"Reversed Vector: $reversedVector")

  val firstThree = vector.take(3)
  println(s"First 3 elements: $firstThree")

  val droppedTwo = vector.drop(2)

  val takenWhileLessThan4 = vector.takeWhile(_ < 4)

  val (left, right) = vector.splitAt(2)

  println(s"Left part: $left, Right part: $right")

}
