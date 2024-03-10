package lang.oop

class SpecialAdd[@specialized(Int, Double) T] {
  def add(x: T, y: T)(implicit num: Numeric[T]): T = num.plus(x, y)
}

object Specialized {

  // Uses the specialized version for Int
  val intAdder = new SpecialAdd[Int]
  println(intAdder.add(1, 2))

  // Uses the specialized version for Double
  val doubleAdder = new SpecialAdd[Double]
  println(doubleAdder.add(1.1, 2.2))

  // Not specialized, as String not being Satisfied by the Numeric Context Bound
  val stringAdder = new SpecialAdd[String]

  // This would not compile because String is not a Numeric type

  // println(stringAdder.add("Hello, ", "World!"))

}
