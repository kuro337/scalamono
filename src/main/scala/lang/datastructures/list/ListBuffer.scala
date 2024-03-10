package lang.datastructures.list

import scala.collection.mutable.ListBuffer

object BufScala {
  def bufUsage(): Unit = {

    val listBuffer = ListBuffer[Long]()

    /// Prepend

    listBuffer.prepend(1L)
    listBuffer.+=:(2L)

    /// Append

    listBuffer += 3L
    listBuffer.append(4L)

    val isEmpty = listBuffer.isEmpty

    listBuffer.clear()

    listBuffer.remove(0)

    for (elem <- listBuffer) println(elem)

  }

}
