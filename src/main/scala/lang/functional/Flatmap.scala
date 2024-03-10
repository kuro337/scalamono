package lang.functional

object FlatmapScala {
  def flatmapUsage(): Unit = {

    val words = List("hello", "world")
    val chars = words.flatMap(word => word.toList)
    println(chars) // Output: List(h, e, l, l, o, w, o, r, l, d)

    val numbers = List(1, 2, 3, 4, 5)
    val squaredEvens =
      numbers.flatMap(n => if (n % 2 == 0) Some(n * n) else None)

    println(squaredEvens) // Output: List(4, 16)

  }
}
