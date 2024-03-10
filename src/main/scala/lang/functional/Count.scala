package lang.functional

object Count {
  def countUsage(): Unit = {

    val l = List(1, 10, 9, 3, 5, 200)

    println(s"Even Numbers: ${l.count(_ % 2 == 0)}")

    val stringVar = "This is a sample string with several 'a's."

    val count = stringVar.count(_ == 'a')

    val numAs = count + "a's : " + "a" * count
    println(numAs)

    // 4a's : aaaa
  }
}
