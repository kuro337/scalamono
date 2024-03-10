package lang.datastructures.mapset

import collection.mutable

object MapApp {
  def scalaMaps(): Unit = {
    println("""Scala Maps:

Map[K,V]    : Immutable Map
mutable.Map : Mutable Map 
   
  var m: Map[Int, Int] = Map()
  
  Map.updated 
  Creates a new map obtained by updating this map with a given key/value pair.
  
  Syntax : updated(K, V) -> Map[K,V]

  Usage:    
  Note: Causes a Runtime error because we dont have Map().withDefaultValue(0)
  m.updated(1, m(1) + 1)

  Using getOrElse 
  m = m.updated(1, m.getOrElse(1, 0) + 1)

Mutable Usage:

mutable.Buffer is a Mutable List in Scala 

  val users = Array("A","B","C","D","D","D","A")

  val map_listvals = mutable.Map[String, mutable.Buffer[Int]]()

  users.zipWithIndex.foreach { case (u, i) =>
      mplis.getOrElseUpdate(u, mutable.Buffer[Int]()).append(i)
  }

    """)
  }

  // Add a value to the list for key 1, or create a new list if key doesn't exist
  def updWith(): Unit = {
    val mutableMap = mutable.Map[Int, List[Int]]()

    mutableMap.updated(1, mutableMap.getOrElse(1, List(2)))

  }
  def mapUsage(): Unit = {
    val m = mutable.Map[String, Int]() // Create an empty mutable Map
    m += ("a" -> 1) // Add a key-value pair
    m += ("b" -> 2) // Add another key-value pair
    m -= "a" // Removing elements

    val keyExists = m.contains("b") // Check if a key exists : Boolean

    val value = m.getOrElse("b", 0)
    println(value) // Output: 2

    val mp = mutable.Map[String, Int]().withDefaultValue(0)

    val users =
      Array("John", "John", "Kate", "K", "Z", "Z", "A", "Z", "Z", "Z", "Nemo")

    users.foreach(u => mp(u) += 1)

    mp.foreach { case (k, v) => print(s"User:$k F:$v | ") }
    println()

    // Sort by Values and return the Array of Keys
    val top_users = mp.toSeq.sortBy(_._2).reverse.take(1).map(_._1).toArray

    /* Maps with Lists as the Key */

    println("Map with Mutable List (mutable.Buffer) as Key")

    // Cleaner Way would be :

    val mplis = mutable.Map[String, mutable.Buffer[Int]]()

    users.zipWithIndex.foreach { case (u, i) =>
      mplis.getOrElseUpdate(u, mutable.Buffer[Int]()).append(i)
    }

    mplis.foreach { case (user, indexes) =>
      println(s"$user : ${indexes.mkString(",")}")
    }

    /* Functional Immutable Way */
    println("Functional Way of Creating a Map[String,List[Int]]")

    // val users = Array("Q", "Q", "K", "K", "Z", "Z", "A", "Z", "Z", "Z", "N")

    val indexes_users = users.zipWithIndex.foldLeft(Map[String, List[Int]]()) {
      case (acc, (u, i)) =>
        // acc.updated(u, acc.getOrElse(u, List()) :+ i) // append
        acc.updated(u, i :: acc.getOrElse(u, List())) // prepend
    }

    indexes_users.foreach { case (u, il) =>
      println(s"$u : ${il.mkString(",")}")
    }

    val mutable_way = users.zipWithIndex
      .foldLeft(mutable.Map[String, mutable.Buffer[Int]]()) {
        case (acc, (u, i)) =>
          acc.getOrElseUpdate(u, mutable.Buffer[Int]()).append(i)
          acc // because foldLeft expects the return type to match Accumulator
      }
      .foreach { case (u, il) => print(s"$u:${il.mkString(",")} ") }

    // Example that doesnt Update it
    val mpl = mutable
      .Map[String, mutable.Buffer[Int]]()
      .withDefaultValue(mutable.Buffer())

    // This doesnt update it - because withDefVal only gives a default Value
    // It wont initialize the Value directly whenever accessed
    users.zipWithIndex.foreach { case (u, i) => mpl(u).append(i) }

    // So we need to do this explicitly
    users.zipWithIndex.foreach { case (u, i) =>
      // Check if the key exists, if not, put a new buffer, then append the index
      if (!mpl.contains(u)) mpl.put(u, mutable.Buffer())
      mpl(u).append(i)
    }

    mpl.foreach { case (user, indexes) =>
      println(s"$user : ${indexes.mkString(",")}")
    }

  }

  def GetTopKElemsFromInput(input: Array[Int], k: Int): Array[Int] = {
    val c = createCounter(input)
    topKElemsFromMap(c, k)
  }

  // toSeq converts a Collection to an Ordered Iterable
  // _._2 when unnamed tuple
  // _2 for named Tuples  : (10, "Scala") | tup._2 // directly accessing 2nd elem

  // we dont know the name of what toSeq returns
  // _._2 for unknown Collections: Map[K,V].toSeq.someOp(_._2)

  def topKElemsFromMap(m: Map[Int, Int], k: Int): Array[Int] = {
    m.toSeq.sortBy(_._2).reverse.take(k).map(_._1).toArray
  }

  def createCounter(nums: Array[Int]): Map[Int, Int] = {
    nums.foldLeft(Map[Int, Int]().withDefaultValue(0)) { (m, n) =>
      m.updated(n, m(n) + 1)
    }

    // Using Mutable Map - non idiomatic

    // val mutab = Map[Int, Int]().withDefaultValue(0)
    // nums.foreach(n => mutab(n) += 1)
    // mutab
  }

}
