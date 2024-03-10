package lang.functional

object FoldScala {
  def foldScalaUsage(): Unit = {

    /*

     foldRight has Params First, then Acc

     arr.foldLeft(Seq.fill(10)(-1)) { case (arrVal,acc) }

     foldLeft has Acc first, then Params (Reversed)

     arr.foldLeft(0){ case (acc, arrVal) => }

     */
    println("""Scala Fold Usage:

Syntax: 

  collection.foldLeft(initialValue)(binaryOperation)

  collection.foldLeft ( initialValue ) ( (valStart , acc) => expression )

  collection.foldRight ( initialVal  ) ( (valEnd   , acc) => expression ) 


  Fold is used to handle Optional Types 

  def fold[B](ifEmpty: => B)(f: A => B): B

  collection.fold ( valIfNone ) { lambda on Some }

""")

    val numbers = Array(1, 2, 3, 4, 5)
    val sum = numbers.foldLeft(0)((acc, n) => acc + n)
    println(sum) // Output: 15

    val chars = List('S', 'c', 'a', 'l', 'a')
    val reversedString = chars.foldRight("")((c, acc) => acc + c)
    println(reversedString) // Output: "alacS"

  }

  // foldLeft with 2 initial Values - we do (X,Y) { case ( (x,y), e) => }
  def maxOperations(nums: Array[Int], k: Int): Int = {
    nums
      .foldLeft((0, Map.empty[Int, Int])) { case ((cnt, mp), num) =>
        if (mp.getOrElse(k - num, 0) > 0)
          (cnt + 1, mp.updated(k - num, mp(k - num) - 1))
        else (cnt, mp.updated(num, mp.getOrElse(num, 0) + 1))
      }
      ._1
  }
  // Counter : Creating an Immutable Map from an Array[Int]
  def createCounter(nums: Array[Int]): Map[Int, Int] = {
    nums.foldLeft(Map[Int, Int]().withDefaultValue(0)) { (m, n) =>
      m.updated(n, m(n) + 1)
    }
  }

}
