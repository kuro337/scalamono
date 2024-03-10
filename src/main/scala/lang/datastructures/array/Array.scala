package lang.datastructures.array

object ArrayApp {

  def combineArrs(a: Array[Int], b: Array[Int]): Array[(Int, Int)] = a.zip(b)

  def arrUsage(): Unit = {
    val arr = Array(10, 20, 30, 40)

    /* findWhere(lambda,startIndex) -> index or -1 */

    val findingInd =
      arr.indexWhere(_ > 30, 0) // Finds  1st index that satisfied condition

    println("""Scala Arrays:

Syntax: 

    val arr = Array(10, 20, 30, 40)

    Initializing with Size 
    val arr_with_len = Array.ofDim[Int] (size)

    Pre Filled 
    val prefilled: Array[Int] = Array.fill(size)(init)

    Matrix 
    val matrix: Array[Array[Int]] = Array.fill(rows)(Array.fill(cols)(0))
""")

    val arr_with_len = Array.ofDim[Int](10)

    println(arr_with_len.mkString)

    println(arr)

    // Array.fill(size)(init_val)

    val a: Array[Int] = Array.fill(10)(5)

    val rows = 10
    val cols = 5

    // Array.fill(rows)(Array.fill(cols)(0))

    val matrix: Array[Array[Int]] = Array.fill(rows)(Array.fill(cols)(0))

    // apply() Accessing the element - after an operation

    arr.sorted.apply(arr.size >> 1)

  }
}
