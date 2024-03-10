package lang.iterators

object Loops {
  def iteration(): Unit = {
    val arr = Array(1, 2, 3, 4, 5, 6, 7)

    val s_rev = (arr.size - 1 to 0 by -1).foldLeft(0) { case (acc, n) =>
      arr(n) + acc
    }

    val s_nor = (0 until arr.size).foldLeft(0) { case (acc, n) => arr(n) + acc }

  }

  def loopComprehension(): Unit = {
    val x = 10
    val y = 5

    for {
      i <- 0 to x
      j <- 0 until y
    } yield {
      println(s"$i $j")
    }

    val result = for {
      i <- 1 to 3
      j <- 1 until 3
    } yield (i, j)

    result.foreach { case (i, j) => println(s"i = $i, j = $j") }

  }
}
