package lang.oop

object EnumUsage {

  sealed trait Color
  case object Red extends Color
  case object Green extends Color
  case object Blue extends Color

  //// Sealed Classes can also be extended!

  sealed trait Shape

  case class Circle(radius: Double) extends Shape
  case class Square(side: Double) extends Shape
  case object NoShape extends Shape

  def describe(shape: Shape): String = shape match {
    case Circle(radius) => s"Circle with radius $radius"
    case Square(side)   => s"Square with side $side"
    case NoShape        => "No shape"
  }

  def usage(): Unit = {
    val color: Color = Red
    println(color) // Output: Red

    val colorName = getColorName(Green)
    println(colorName)

  }

  def getColorName(color: Color): String = color match {
    case Red   => "The color is Red"
    case Green => "The color is Green"
    case Blue  => "The color is Blue"
  }

}
