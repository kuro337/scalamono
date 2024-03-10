package lang.strings
import scala.util.Try

object StringsUsage {
  def stringsUsage(): Unit = {

    val a = 10
    val b = 5

    /* String Concat */

    println("1s:" + ("1" * a) + "\n0s:" + ("0" * b))

    val someStr = "Hello World"
    val atMid = someStr.charAt(3)

    val cap = someStr.toUpperCase
    val low = someStr.toLowerCase
    val nospaces = someStr.trim

    val words = "Hello, World! This is Scala.".split(" ")
    // Hello,, World!, This, is, Scala.)

    val startsWithHello = someStr.startsWith("Hello") // true

    val stringList =
      List("34,23948", "34,25095", "34,24516") // Example list of strings

    val replacedList = stringList.map(_.replaceAll(",", "."))

    println(replacedList)

    val doublesList = replacedList.map(_.toDouble)

    println(doublesList)

    // .toOption returns Option[Double] - as try can return Some(val) or None
    // .flatMap will flatten Option[Double] to Double and discard None's

    val doubles_list = stringList.flatMap(s =>
      scala.util.Try(s.replaceAll(",", ".").toDouble).toOption
    )

    val tostr = safeStringToInt("wont convert").getOrElse(0)
    val succ = safeStringToInt("1000").getOrElse(0)

    val d = "123.45".toDouble
    val f = "123.45".toFloat

  }

  def safeStringToInt(s: String): Option[Int] = Try(s.toInt).toOption
}
