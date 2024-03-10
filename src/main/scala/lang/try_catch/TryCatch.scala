package lang.try_catch

/*

try { someOp } catch { case ()}


 */
object TryCatch {
  def tryCatchUsage(): Unit = {
    val result1 = divide(10, 2)
    println(result1) // Output: 5

    val result2 = divide(10, 0)
    println(result2)

  }

  def divide(a: Int, b: Int): Int = {
    try {
      a / b
    } catch {
      case ex: ArithmeticException =>
        println("Error: Division by zero!")
        0
      case ex: Exception =>
        println(s"Error: ${ex.getMessage}")
        0
    } finally {
      println("Division operation completed.")
    }
  }

}
