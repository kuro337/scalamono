package lang.functional

object Scan {
  /*

    Collection.scanLeft(init)((acc, elem) => acc + elem)

    Collection.scanLeft(0) ( _ + _ )
    Collection.scanLeft(0) { _ + _ }

   */

  def scanScala(): Unit = {

    val nums = Array(1, 2, 3, 4, 5)

    val runningTotals = nums.scanLeft(0)(_ + _)

    // 0, 1, 3, 6, 10, 15

    println(s"Running totals from left: ${runningTotals.mkString(", ")}")

    val runningTotalsRight = nums.scanRight(0)(_ + _)

    // 15, 14, 12, 9, 5, 0

    println(s"Running totals from right: ${runningTotalsRight.mkString(", ")}")

  }
}
