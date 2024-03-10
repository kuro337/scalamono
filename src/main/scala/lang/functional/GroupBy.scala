package lang.functional

object GroupByScala {

  def getArrItemsCounts(arr: Array[Int]): Map[Int, Int] = {
    arr.groupBy(identity).mapValues(_.size).toMap

  }
  // Collection.groupBy(identity) creates a Map items mapped to themselves (with dupes in the Value)
  def groupByUsage: Unit = {

    // Map ( 5 -> ["hello","wordl","scala"] , 2 -> ["is","fun"] )

    val words = List("hello", "world", "scala", "is", "fun")

    val groupedByLength = words.groupBy(_.length)

    // Map(1 -> Array(1), 2 -> Array(2, 2), 3 -> Array(3, 3, 3))

    val numbersWithDuplicates = Array(1, 2, 2, 3, 3, 3)

    val groupedByIdentity = numbersWithDuplicates.groupBy(identity)

  }
}
