package lang.numeric

import scala.math.Numeric.Implicits._
import scala.util.Random

object RandomScala {

  /* Creates a Random Int between Int.MinValue <-> Int.MaxValue */
  def randomInt: Int = scala.util.Random.nextInt

  /// 0 <= [T] < upper
  def randIntUpperBound(upper: Int): Int = scala.util.Random.nextInt(upper)

  /// l <= [T] <= r
  def randIntRange(l: Int, r: Int): Int = scala.util.Random.nextInt(r - l)

  def randomDouble(): Double = scala.util.Random.nextDouble
  def randomFloat(): Float = scala.util.Random.nextFloat
  def randomLong(): Long = scala.util.Random.nextLong

  val random: scala.util.Random = new scala.util.Random()

  val a = random.nextInt()
  val b = random.nextInt(10)
  val c = random.nextInt(100)

  /* Generics */

  val intRand = new GenericRand[Int].rand(1, 10)

  val doubleRand = new GenericRand[Double].rand(1.0, 10.0)

  val floatRand = new GenericRand[Float].rand(1.0f, 10.0f)

}

class GenericRand[T](implicit val randomGen: RandomGen[T]) {
  def rand(x: T, y: T): T = randomGen.rand(x, y)
}

trait RandomGen[T] {
  def rand(x: T, y: T): T
}

object RandomGen {
  implicit val intRandomGen: RandomGen[Int] = new RandomGen[Int] {
    def rand(x: Int, y: Int): Int = scala.util.Random.nextInt(y - x)
  }

  implicit val floatRandomGen: RandomGen[Float] = new RandomGen[Float] {
    def rand(x: Float, y: Float): Float =
      x + scala.util.Random.nextFloat() * (y - x)
  }

  implicit val doubleRandomGen: RandomGen[Double] = new RandomGen[Double] {
    def rand(x: Double, y: Double): Double =
      x + scala.util.Random.nextDouble() * (y - x)
  }
}
