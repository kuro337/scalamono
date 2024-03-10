package lang.oop

object CaseApp {
  def caseUsage(): Unit = {
    case class User(name: String, age: Int)

    // Create instances of the case class
    val user1 = User("Alice", 30)
    val user2 = User("Bob", 25)
    val user3 = User("Alice", 30)

    // Compare instances for equality
    println(user1 == user2) // Prints: false
    println(user1 == user3) // Prints: true
    // Copy an instance, optionally changing some fields
    val user4 = user1.copy(age = 31)
    // Automatically generated toString method
    println(user4) // Prints: User(Alice,31)

    val f1 = Freq("aaabbb")
    val f2 = Freq("bbbaaa")

    println(f1)

    val eq = (f1 == f2)
    if (eq) println("f1 and f2 equal")

    f1.printFreq()

  }

  case class Freq(s: String) {

    private val c: Array[Int] = {
      val freq = Array.ofDim[Int](26)
      s.foreach(ch => freq(ch - 'a') += 1)
      freq
    }

    def printFreq(): Unit = println(c.mkString(","))
    def getFreqArr(): Array[Int] = c

    override def equals(obj: Any): Boolean = {
      obj match {
        case other: Freq => this.c sameElements other.c
        case default     => false
      }
    }

    override def hashCode(): Int = {
      java.util.Arrays.hashCode(c)
    }
  }
}
