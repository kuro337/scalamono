package lang.datastructures.mapset
import collection.mutable.Set

object SetApp {
  def mutableSetScala(): Unit = {
    /* Immutable Operations */
    val st = Set(1, 2, 3, 4, 5)

    val new_set = st -- List(3, 4, 5)

    val s_added = st ++ List(6, 7, 8)

    val intersection = st & Set(1, 3, 5)

    val union = Set(1, 2, 3, 4, 5) | Set(3, 4, 5, 6, 7)

    // Creating a new mutable Set and adding elements
    val s = Set[Int]() // Create an empty mutable Set
    s += 10 // Add an element
    s += 20 // Add another element

    // Removing elements
    s -= 10 // Remove an element

    // Check if an element exists
    val exists = s.contains(20) // true if 20 is in the set

    println(s) // Output: Set(20)
    println(exists) // Output: true

  }
}
