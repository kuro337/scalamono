package lang.math

object General {

  def general(): Unit = {
    val a = 10
    val b = -20
    val bigger = a.abs max b.abs
  }

  // Returns the Max/Min Sum of a Subarray
  def KadanesMin(arr: Array[Int]): Int =
    arr
      .drop(1)
      .foldLeft((arr(0), arr(0))) { case ((minSum, curr), num) =>
        (minSum min (curr min curr + num), curr min curr + num)
      }
      ._1

  def KadanesMax(arr: Array[Int]): Int = {
    if (arr.size == 1) return arr(0)
    var mx = arr(0)
    var curr = arr(0)
    for (i <- 1 until arr.size) {
      curr = (arr(i) + curr) max arr(i)
      mx = curr max mx
    }
    mx
  }
  // Sum of the first N consecutive numbers is N*(N+1)/2
  def gaussFormula(n: Int): Int = n * (n + 1) / 2

  def sumNumsBetweenRangeInclusive(l: Int, r: Int): Int =
    gaussFormula(r) - gaussFormula(l)

  /// Each Number can only have 1 Factor Greater than its' Square Root
  /// https://codeforces.com/blog/entry/22317 and check Sieve
  def findLargestPrimeFactor(n: Long): Long = {
    var maxP = -1L
    var num = n

    // divide out 2's and other known primes
    val knownPrimes = List(2, 3, 5, 7, 11, 13, 17, 19, 23, 29)
    for (prime <- knownPrimes) {
      while (num % prime == 0) {
        maxP = prime
        num /= prime
      }
    }

    // Start checking from the next prime after those handled explicitly
    var f = 31
    while (f * f <= num) {
      while (num % f == 0) {
        maxP = f
        num /= f
      }
      f += 2
    }

    // In the case the num itself was Prime - we won't have reached dividing it since
    // we only check till the sq root of the number
    if (num > 2) num else maxP
  }

  /* Sum of an Arithmetic Series
  The sum S of the first k terms of an arithmetic series is (k/2) * (first + last) where:
    first -> 1st item in the series
    last  -> last item in the series
    k     -> number of items in the series
    // 55
    val sum = arithmeticSeriesSum(1, 10, 10) // k is 10 because we're counting from 1 to 10
   */
  def arithmeticSeriesSum(first: Int, last: Int, k: Int): Int =
    k / 2 * (first + last)

  /// (3,10) -> and want Multiples of 3 < 10 , so upto 9 -> 9/3 has 3 elements
  /// Once we get # of elements -> we can get their sum using 3 as k
  def sumOfMultiples(a: Int, t: Int): Int = {
    val k = (t - 1) / a
    val last = a * k
    arithmeticSeriesSum(a, last, k)
  }

}
