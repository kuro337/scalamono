package lang.math

object BigIntScala {

  // import scala.math.BigInt

  def getFactorial(n: Int): BigInt =
    (1 to n).foldLeft(BigInt(1))((acc, n) => acc * n)

  def bigIntUsage: Unit = {

    val bigIntFromLong = BigInt(1234567890123456789L)
    val bigIntFromString = BigInt("123456789012345678901234567890")

    val a = BigInt("12345678901234567890")
    val b = BigInt("98765432109876543210")

    val sum = a + b
    val difference = a - b
    val product = a * b
    val quotient = b / a
    val remainder = b % a

    val c = BigInt("12345678901234567890")

    // Raise c to the power of 2
    val power = c.pow(2)

    // Greatest common divisor of c and the sum
    val gcd = c.gcd(sum)

    // Checking if a number is a probable prime
    val isPrime = c.isProbablePrime(100) // The certainty can be adjusted

  }
}
