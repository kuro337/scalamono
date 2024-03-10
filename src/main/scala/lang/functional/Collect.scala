package lang.functional

object CollectScala {
  def collectUsage(): Unit = {
    val nums = Array(2, 4, 1, 3, 5, 8, 12, 16, 20, 24, 23, 22)

    val divisible_by_four = nums.collect { case num if ((num & 3) == 0) => num }

    println(s"Nums Divisible by 4 ${divisible_by_four.mkString("-")}")

    val div_by_four = nums.filter(e => (e & 3) == 0)

    println(s"Nums Divisible by 4 ${div_by_four.mkString("-")}")

  }
}
