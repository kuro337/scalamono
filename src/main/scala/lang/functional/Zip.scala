package lang.functional

object Zip {

  @inline def keepOddIndexes(arr: Array[Int]): Array[Int] =
    arr.zipWithIndex.filter { case (_, i) => (i & 1) == 1 }.map(_._1)

  @inline def keepEvenIndexes(arr: Array[Int]): Array[Int] =
    arr.zipWithIndex.filter(_._2 % 2 == 0).map { _._1 }

}
