package lang.math

object Primes {

  /* Finds All Prime Numbers upto N -> Sieve of Eratosthenes */
  def findAllPrimeNumbers(n: Long): Array[Long] = {
    val markNonPrimes = Array.ofDim[Long](n.toInt + 1)
    (0 to n.toInt).foreach(i => markNonPrimes(i) = i)

    /// start from 2 and mark all even nums as non prime - then move on to Odd Nums
    var i = 2
    while (i * i <= n) {
      var j = 2
      while (i * j <= n) {
        markNonPrimes(i * j) = -1
        j += 1
      }
      if (i == 2) i += 1 else i += 2
    }

    /// 0 and 1 are not considered prime so remove them, and the ones we know are not prime
    markNonPrimes.drop(2).filter(_ != -1)

  }

  // If we care about finding particular primes that are Factors of a num this is space efficient
  def findLargestPrimeFactor(n: Long): Long = {

    // if n is even we already know 2 will be a prime factor of it
    var maxPrime = if ((n & 1) == 0) 2 else -1L
    var num = n

    // Divide out all 2s first, so we can skip even numbers later

    while ((num & 1) == 0) num >>= 1

    // Now we only need to test odd numbers
    var factor = 3L
    while (factor * factor <= num) {
      while (num % factor == 0) {
        maxPrime = factor
        num /= factor
      }
      factor += 2
    }

    /// we only divide out until the Sqrt of n -
    /// so in the case that n itself was prime - we handle that case

    if (num > 2) maxPrime = num

    maxPrime
  }
}
