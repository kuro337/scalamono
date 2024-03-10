package lang.datastructures.list

object ZipList {

  def zipLists(a: List[Int], b: List[Int]): List[(Int, Int)] = a.zip(b)

  def f(coefficients: List[Int], powers: List[Int], x: Double): Double =
    coefficients
      .zip(powers)
      .map { case (coeff, power) => coeff * math.pow(x, power) }
      .sum

  def area(coefficients: List[Int], powers: List[Int], x: Double): Double =
    math.Pi * math.pow(f(coefficients, powers, x), 2)

}
