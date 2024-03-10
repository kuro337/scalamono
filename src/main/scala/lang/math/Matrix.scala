package lang.math

object MatrixMath {

  def flippingMatrix(matrix: Array[Array[Int]]): Int = {
    val n = matrix.length / 2
    var sum = 0
    for (i <- 0 until n) {
      for (j <- 0 until n) {
        // Identify the four cells that can be flipped into the current cell's position
        val topLeft = matrix(i)(j)
        val topRight = matrix(i)(2 * n - j - 1)
        val bottomLeft = matrix(2 * n - i - 1)(j)
        val bottomRight = matrix(2 * n - i - 1)(2 * n - j - 1)

        val maxVal = Seq(topLeft, topRight, bottomLeft, bottomRight).max
        sum += maxVal
      }
    }
    sum
  }

}
