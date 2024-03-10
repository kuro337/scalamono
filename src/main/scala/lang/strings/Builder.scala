package lang.strings

import scala.collection.mutable

object StrBuilder {

  def strBuilderScala(): Unit = {

    val s = new StringBuilder("Hello")

    // or val k = new mutable.StringBuilder

    s += '-'

    s ++= "World"

    s.insert(0, "StringBuilder Usage:\n")

    s.delete(0, 6)

    s.deleteCharAt(5)

    s.clear()

    s.append("hello")

    println(s.toString)

  }
}
