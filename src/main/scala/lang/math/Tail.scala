package lang.math

// if n is 0 , return n , otherwise decr n by 1, a is curr b , and c is a+b
// a,b represents the 2 numbers before n

object TailRecursion {

  @scala.annotation.tailrec
  def Fib(n: Int, a: Int = 0, b: Int = 1): Int = n match {
    case 0 => a
    case _ => Fib(n - 1, b, a + b)
  }

}
