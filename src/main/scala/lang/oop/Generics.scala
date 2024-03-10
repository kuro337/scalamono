package lang.oop

/*
1. Define a Trait for our Type Class

trait ForGeneric[T] {
  def method(x: T, y: T): T
}

2. Provide Implicit Instances

object ForGeneric {
  implicit val forInt: ForGeneric[Int] = new ForGeneric[Int] {
    def method(x: Int, y: Int): Int = x + y // Example operation for Int
  }
}

3. Provide Generic Class/Func

class CallerObj[T](implicit val someParam: ForGeneric[T]) {
  def method(x: T, y: T): T = someParam.method(x, y)
}

4. Usage
val x = 10
val y = 20

val result = new CallerObj[Int].method(x, y)


> class CallerObj[T](implicit val someParam: ForGeneric[T]) {

- class x[T] - needs a Type Param
- implicit val someParam : ForGeneric[T] ->  compiler uses any Compatible Values
                                             in Scope automatically

 */

//// Trait for Type Class T
trait RandomNumeric[T] {
  def rand(x: T, y: T): T
}

//// Provide Concrete Impls of the Trait for Specific Types
object RandomNumeric {
  implicit val forInt: RandomNumeric[Int] = new RandomNumeric[Int] {
    def rand(x: Int, y: Int): Int = scala.util.Random.nextInt()
  }

  implicit val forDouble: RandomNumeric[Double] = new RandomNumeric[Double] {
    def rand(x: Double, y: Double): Double = scala.util.Random.nextDouble()
  }
}

//// Generic Class
class NumObj[T](implicit val num: RandomNumeric[T]) {
  def rand(x: T, y: T): T = num.rand(x, y)
}

//// Usage
object Generics {
  val randInt = new NumObj[Int].rand(0, 10)

  val randDoub = new NumObj[Double].rand(0, 10)

}
