package lang.functional

object Flatten {

  def flatList(): Unit = {

    val n = 5
    val l = List(1, 2, 3, 4, 5)

    /* (1,1,1,1,1,2,2,2,2,2,3,3......... ,5) */

    val flat = l.map(List.fill(n)(_)).flatten

    val flat_b = l.flatMap(List.fill(n)(_))

  }
}
