package lang.datastructures.hashmap

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

/* HashMap is just a Concrete Implementation of Map , whereas Map has runtime dynamic attributes -> more flexible */
object HashMapScala {
  def hashmapUsage(): Unit = {
    val h = HashMap[Int, Int]()

    val he = HashMap.empty[Int, Int]

    h(1) = 10
    h(20) = 20

    // Simply returns - doesnt update the Map
    println(h.getOrElse(3, 30))

    // If Key doesnt exist - updates the map with default Value
    println(h.getOrElseUpdate(4, 40))

    h.foreach { case (k, v) => println(s"$k-$v") }

    val m = Map[Int, Int]()

    m(1) = 10
    m(20) = 20

  }
}
