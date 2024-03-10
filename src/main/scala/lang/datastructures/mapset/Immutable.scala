package lang.datastructures.mapset

object ImmutableApp {
  def defaultMapScala(): Unit = {

    // Immutable Set
    val immutableSet = Set(1, 2, 3)
    val newSet = immutableSet + 4 // Creates a new Set with 4 added
    val removedSet = newSet - 2 // Creates a new Set with 2 removed

// Immutable Map
    val immutableMap = Map("a" -> 1, "b" -> 2)
    val newMap =
      immutableMap + ("c" -> 3) // Creates a new Map with "c" -> 3 added
    val removedMap = newMap - "a" // Creates a new Map with "a" removed
    val defaultValue =
      immutableMap.getOrElse("d", 0) // Get value for "d" or default to 0

    println("""Scala Default Map Usage:

Syntax: 
  var m: Map[Int, Int] = Map()
  
  Map.updated
  Creates a new map obtained by updating this map with a given key/value pair.
  
  Syntax : updated(K, V) -> Map[K,V]

  Usage: 
    
  Causes a Runtime error because we dont have Map().withDefaultValue(0)
  m.updated(1, m(1) + 1)

  Using getOrElse 
  m = m.updated(1, m.getOrElse(1, 0) + 1)

""")

    var m: Map[Int, Int] = Map()
    // causes runtime error because we dont have Map().withDefaultValue(0)
    // m.updated(1, m(1) + 1)
    m = m.updated(1, m.getOrElse(1, 0) + 1)

    println(newSet) // Output: Set(1, 2, 3, 4)
    println(removedSet) // Output: Set(1, 3, 4)
    println(newMap) // Output: Map(a -> 1, b -> 2, c -> 3)
    println(removedMap) // Output: Map(b -> 2, c -> 3)
    println(defaultValue) // Output: 0

  }
}
