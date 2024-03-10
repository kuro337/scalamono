package lang.math

object Decimals {
  def decimalUsage(): Unit = {
    val number = 123.456789

    printf("%.2f\n", number)

    val sum = 10

    println(s"${5.0 / sum}")

    // or
    val some_num: Double = 10
    println(s"${some_num / 20}")
  }
}
