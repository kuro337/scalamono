package lang.functional

object Sliding {
  def slidingWindow(): Unit = {

    val l = List(10, 8, 20, 22, 1, 3, 0, -1, 44, 211)

    val isIncreasing = l.sliding(2).forall {
      case Seq(a, b) => a < b
      case _ =>
        true
      // Edge case when last window might have only 1 elem
    }

    val isIncreasingDirect =
      l.sliding(2).forall(window => window(0) < window(1))

  }
}
