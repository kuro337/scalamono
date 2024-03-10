package lang.datastructures.list

object SeqScala {
  def seqUsage(): Unit = {
    val n = 10
    val s = Seq.fill(10)(Int.MaxValue)

    /* Updated first elem to -1 and return new list */

    val u = s.updated(0, -1)

  }
}
