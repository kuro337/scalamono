package lang.functional

object Reduce {
  def reduceUsage(): Unit = {
    val nums = List(1, 2, 3, 4, 5, 6)

    val product = nums.reduce(_ * _)

    val prod = nums.reduce((a, b) => a * b)

  }

}
